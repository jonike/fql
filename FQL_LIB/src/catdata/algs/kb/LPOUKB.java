package catdata.algs.kb;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import catdata.Chc;
import catdata.DAG;
import catdata.Pair;
import catdata.Quad;
import catdata.Triple;
import catdata.Util;
import catdata.algs.kb.KBExp.KBApp;
import catdata.algs.kb.KBExp.KBVar;


/**
 * 
 * @author Ryan Wisnesky
 *
 * Implements "unfailing" aka "ordered" Knuth-Bendix completion.  Handles empty sorts correctly.
 * 
 * Note: will not orient var = const
 * 
 * Update Jan 16: add special support for associative and commutative theories as described in
 * "On Using Ground Joinable Equations in Equational Theorem Proving"
 * 
 * Update Oct 16: change E-reduction to instantiate free variables with a minimal constant,
 *  as described in 'Decision Problems in Ordered Rewriting'.  This is necessary to use only-ground complete
 *  systems as decision procedures via herbrandization.  This means E-reduction is LPO specific. 
 *
 * @param <C> the type of functions/constants
 * @param <V> the type of variables
 * @param <T> the type of types
 */
public class LPOUKB<T, C, V> extends DPKB<T, C, V> {
	
	private void inhabGen(Set<T> inhabited) {
		while (inhabGen1(inhabited));
	}
	
	private boolean inhabGen1(Set<T> ret) {
		boolean changed = false;
		for (C c : sig.keySet()) {
			for (T t : sig.get(c).first) {
				if (!ret.contains(t)) {
					continue;
				}
			}
			changed = changed | ret.add(sig.get(c).second);
		}
		return changed;
	}
	
	private Set<T> groundInhabited = new HashSet<>();
	
	private List<C> prec;

	private boolean isComplete = false;
	private boolean isCompleteGround = false;

	//order matters
	private List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> R, E, G; 

	private Iterator<V> fresh;

	private Set<Pair<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>>> seen = new HashSet<>();

	private Map<Chc<V, C>, List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>>> AC_symbols;

	private KBOptions options;

	private Map<T, Chc<V, C>> min = new HashMap<>();
	
	private Map<C, Pair<List<T>, T>> sig;
	
	public LPOUKB(Set<Triple<KBExp<C, V>, KBExp<C, V>, Map<V, T>>> E0, Iterator<V> fresh, Set<Triple<KBExp<C, V>, KBExp<C, V>, Map<V, T>>> R0, KBOptions options, List<C> prec, Map<C, Pair<List<T>, T>> sig, Set<T> tys) throws InterruptedException {
		this.options = options;
		this.prec = prec;
		this.R = new LinkedList<>();
		this.sig = sig;
		for (Triple<KBExp<C, V>, KBExp<C, V>, Map<V, T>> r : R0) {
			R.add(freshen(fresh, new Triple<>(r.first.inject(), r.second.inject(), r.third)));
		}
		this.fresh = fresh;
		this.E = new LinkedList<>();
		for (Triple<KBExp<C, V>, KBExp<C, V>, Map<V, T>> e : E0) {
			E.add(freshen(fresh, new Triple<>(e.first.inject(), e.second.inject(), e.third)));
		}
		this.G = new LinkedList<>();
		initAC();
		for (T t : tys) {
			V v = fresh.next();
			min.put(t, Chc.inLeft(v));
		}
		inhabGen(groundInhabited);
		complete();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void initAC() throws InterruptedException {
		if (!options.semantic_ac) {
			return;
		}
		AC_symbols = new HashMap<>();
		outer: for (C f : sig.keySet()) {
			if (sig.get(f).first.size() != 2) {
				continue;
			}
			T t1 = sig.get(f).first.get(0);
			T t2 = sig.get(f).first.get(1);
			T t3 = sig.get(f).second;
			if (!(t1.equals(t2) && t2.equals(t3))) {
				continue;
			}
			boolean cand1_found = false;
			boolean cand2_found = false;
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> cands = AC_E(f);
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand1 = cands.get(0);
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand2 = cands.get(1);
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> other : E) {
				if (subsumes(cand1, other)) {
					cand1_found = true;
				} else if (subsumes(cand1, other.reverse12())) {
					cand1_found = true;
				}
				if (subsumes(cand2, other)) {
					cand2_found = true;
				} else if (subsumes(cand2, other.reverse12())) {
					cand2_found = true;
				}
				if (cand1_found && cand2_found) {
					List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> l = new LinkedList<>();
					l.add(AC_E(f).get(1)); // assoc rewrite rule
					l.add(AC_E(f).get(0)); // comm eq
					l.addAll(AC_E0(f)); // perm eqs
					AC_symbols.put(Chc.inRight(f), l);
					continue outer;
				}
			}
		}
	}

	private KBExp<Chc<V, C>, V> achelper(C f, V xx, V yy, V zz) {
		KBExp<Chc<V, C>, V> x = new KBVar<>(xx);
		KBExp<Chc<V, C>, V> y = new KBVar<>(yy);
		KBExp<Chc<V, C>, V> z = new KBVar<>(zz);
		List<KBExp<Chc<V, C>, V>> yz = new LinkedList<>();
		yz.add(y);
		yz.add(z);
		List<KBExp<Chc<V, C>, V>> xfyz = new LinkedList<>();
		xfyz.add(x);
		xfyz.add(new KBApp<>(Chc.inRight(f), yz));
		return new KBApp<>(Chc.inRight(f), xfyz);
	}

	private List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> AC_E0(C f) {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> ret = new LinkedList<>();
		V x = fresh.next();
		V y = fresh.next();
		V z = fresh.next();
		Map<V, T> ctx = new HashMap<>();
		T t = sig.get(f).second;
		ctx.put(x, t);
		ctx.put(y, t);
		ctx.put(z, t);

		ret.add(freshen(fresh, new Triple<>(achelper(f, x, y, z), achelper(f, y, x, z), ctx)));
		ret.add(freshen(fresh, new Triple<>(achelper(f, x, y, z), achelper(f, z, y, x), ctx)));
		ret.add(freshen(fresh, new Triple<>(achelper(f, x, y, z), achelper(f, y, z, x), ctx)));

		return ret;
	}

	private List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> AC_E(C f) {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> ret = new LinkedList<>();
		V v1 = fresh.next();
		V v2 = fresh.next();
		KBExp<Chc<V, C>, V> x = new KBVar<>(v1);
		KBExp<Chc<V, C>, V> y = new KBVar<>(v2);
		List<KBExp<Chc<V, C>, V>> xy = new LinkedList<>();
		xy.add(x);
		xy.add(y);
		List<KBExp<Chc<V, C>, V>> yx = new LinkedList<>();
		yx.add(y);
		yx.add(x);
		Map<V, T> ctx = new HashMap<>();
		T t = sig.get(f).second;
		ctx.put(v1, t);
		ctx.put(v2, t);
		ret.add(new Triple<>(new KBApp<>(Chc.inRight(f), xy), new KBApp<>(Chc.inRight(f), yx), ctx));

		v1 = fresh.next();
		v2 = fresh.next();
		x = new KBVar<>(v1);
		y = new KBVar<>(v2);
		V v3 = fresh.next();
		KBExp<Chc<V, C>, V> z = new KBVar<>(v3);
		List<KBExp<Chc<V, C>, V>> yz = new LinkedList<>();
		yz.add(y);
		yz.add(z);
		xy = new LinkedList<>();
		xy.add(x);
		xy.add(y);
		List<KBExp<Chc<V, C>, V>> xfyz = new LinkedList<>();
		xfyz.add(x);
		xfyz.add(new KBApp<>(Chc.inRight(f), yz));
		List<KBExp<Chc<V, C>, V>> fxyz = new LinkedList<>();
		fxyz.add(new KBApp<>(Chc.inRight(f), xy));
		fxyz.add(z);
		ctx = new HashMap<>();
		ctx.put(v1, t);
		ctx.put(v2, t);
		ctx.put(v3, t);
		ret.add(new Triple<>(new KBApp<>(Chc.inRight(f), fxyz), new KBApp<>(Chc.inRight(f), xfyz), ctx));

		return ret;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static <C, V, T> Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> freshen(Iterator<V> fresh, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> eq) {
		Quad<Map<V, KBExp<Chc<V, C>, V>>, Map<V, KBExp<Chc<V, C>, V>>, Map<V,V>, Map<V,V>> xxx = freshenMap(fresh, eq);
		Map<V, KBExp<Chc<V, C>, V>> subst = xxx.first;
		return new Triple<>(eq.first.subst(subst), eq.second.subst(subst), subst(eq.third, xxx.third));
	}

	private static <V,T> Map<V, T> subst(Map<V, T> m, Map<V, V> subst) {
		Map<V, T> ret = new HashMap<>();
		
		for (V v : m.keySet()) {
			V vv = subst.get(v);
			if (vv == null) {
				ret.put(v, m.get(v));
			} else {
				ret.put(vv, m.get(v));
			}
		}
		
		return ret;
	}
	
	private static <C, V, T> Quad<Map<V, KBExp<Chc<V, C>, V>>, Map<V, KBExp<Chc<V, C>, V>>, Map<V,V>, Map<V,V>> freshenMap(Iterator<V> fresh, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> eq) {
		Set<V> vars = new HashSet<>();
		KBExp<Chc<V, C>, V> lhs = eq.first;
		KBExp<Chc<V, C>, V> rhs = eq.second;
		vars.addAll(lhs.vars());
		vars.addAll(rhs.vars());
		Map<V, KBExp<Chc<V, C>, V>> subst = new HashMap<>();
		Map<V, KBExp<Chc<V, C>, V>> subst_inv = new HashMap<>();
		Map<V, V> subst1 = new HashMap<>();
		Map<V, V> subst_inv1 = new HashMap<>();
		for (V v : vars) {
			V fr = fresh.next();
			subst.put(v, new KBVar<>(fr));
			subst_inv.put(fr, new KBVar<>(v));
			subst1.put(v, fr);
			subst_inv1.put(fr, v);
		}
		return new Quad<>(subst, subst_inv, subst1, subst_inv1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static <X> void remove(Collection<X> X, X x) {
		while (X.remove(x));
	}

	private static <X> void add(Collection<X> X, X x) {
		if (!X.contains(x)) {
			X.add(x);
		}
	}

	private static <X> void addFront(List<X> X, X x) {
		if (!X.contains(x)) {
			X.add(0, x);
		}
	}

	private static <X> void addAll(Collection<X> X, Collection<X> x) {
		for (X xx : x) {
			add(X, xx);
		}
	}

	private void sortByStrLen(List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> l) {
		if (!options.unfailing) {
			l.sort(ToStringComparator);
		} else {
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> unorientable = new LinkedList<>();
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> orientable = new LinkedList<>();
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> k : l) {
				if (orientable(k)) {
					orientable.add(k);
				} else {
					unorientable.add(k);
				}
			}
			orientable.sort(ToStringComparator);
			l.clear();
			l.addAll(orientable);
			l.addAll(unorientable);
		}
	}

	private void checkParentDead() throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException("Precedence tried: " + prec);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void complete() throws InterruptedException {
		while (!step());
		
		if (!isCompleteGround) {
			throw new RuntimeException("Not ground complete after iteration timeout.  Last state:\n\n" + toString());
		}
	}

	private boolean subsumes(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> other) throws InterruptedException {
		return (subsumes0(cand, other) != null);
	}

	private Map<V, KBExp<Chc<V, C>, V>> subsumes0(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> other) throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> candX = cand;

		if (!Collections.disjoint(candX.first.vars(), other.first.vars()) || !Collections.disjoint(candX.first.vars(), other.second.vars()) || !Collections.disjoint(candX.second.vars(), other.first.vars()) || !Collections.disjoint(candX.second.vars(), other.second.vars())) {
			candX = freshen(fresh, cand);
		}

		Chc<V,C> xxx = Chc.inLeft(fresh.next());
		List<KBExp<Chc<V, C>, V>> l = new LinkedList<>();
		l.add(candX.first);
		l.add(candX.second);
		KBApp<Chc<V, C>, V> cand0 = new KBApp<>(xxx, l);

		List<KBExp<Chc<V, C>, V>> r = new LinkedList<>();
		r.add(other.first);
		r.add(other.second);
		KBApp<Chc<V, C>, V> other0 = new KBApp<>(xxx, r);

		Map<V, KBExp<Chc<V, C>, V>> subst = findSubst(other0, cand0, other.third, Util.union(groundInhabited, cand.third.values()));
		return subst;
	}

	private List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> filterSubsumed(Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CPX) throws InterruptedException {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CP = new LinkedList<>();
		outer: for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand : CPX) {
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : E) {
				if (subsumes(cand, e)) {
					continue outer;
				}
			}
			CP.add(cand);
		}
		return CP;
	}

	private List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> filterSubsumedBySelf(Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CPX) throws InterruptedException {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CP = new LinkedList<>(CPX);

		Iterator<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> it = CP.iterator();
		while (it.hasNext()) {
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> cand = it.next();
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : CP) {
				if (cand.equals(e)) {
					continue;
				}
				if (subsumes(cand, e)) {
					it.remove();
					break;
				}
				if (subsumes(cand.reverse12(), e)) {
					it.remove();
					break;
				}
				if (subsumes(cand, e.reverse12())) {
					it.remove();
					break;
				}
				// TODO: this one redundant?
				if (subsumes(cand.reverse12(), e.reverse12())) {
					it.remove();
					break;
				}
			}
		}
		return CP;
	}

	// is also compose2
	// simplify RHS of a rule
	private void compose() throws InterruptedException {
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> to_remove = null;
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> to_add = null;
		do {
			to_remove = null;
			to_add = null;
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r : R) {
				Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> R0 = new HashSet<>(R);
				R0.remove(r);
				KBExp<Chc<V, C>, V> new_rhs = red(null, Util.append(E, G), R0, r.second, r.third, Collections.emptyMap());
				if (!new_rhs.equals(r.second)) {
					to_remove = r;
					to_add = new Triple<>(r.first, new_rhs, r.third);
					break;
				}
			}
			if (to_remove != null) {
				R.remove(to_remove);
				R.add(to_add);
			}
		} while (to_remove != null);
	}

	//TODO: caching might be unsound here - look into and reactivate if possible
	private KBExp<Chc<V, C>, V> red(Map<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>> cache, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> Ex, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> Ry, KBExp<Chc<V, C>, V> e, Map<V,T> ctx, Map<V,T> sks) throws InterruptedException {
		Set<T> inhab = new HashSet<>(groundInhabited);
		inhab.addAll(ctx.values());
		inhab.addAll(sks.values());
		inhabGen(inhab);

		for (;;) {
			KBExp<Chc<V, C>, V> e0 = step(null, Ex, Ry, e, inhab);
			if (e.equals(e0)) {
				if (cache != null) {
					cache.put(e0, e);
				}
				return e0;
			}
			e = e0;
		}
	}

	private KBExp<Chc<V, C>, V> step(Map<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>> cache, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> E, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> R, KBExp<Chc<V, C>, V> ee, Set<T> inhab) throws InterruptedException {
		if (ee.isVar) {
			return step1(cache, E, R, ee, inhab);
		} else {
			KBApp<Chc<V, C>, V> e = ee.getApp();
			List<KBExp<Chc<V, C>, V>> args0 = new LinkedList<>();
			for (KBExp<Chc<V, C>, V> arg : e.args) {
				args0.add(step(cache, E, R, arg, inhab)); //must be step
			}
			KBApp<Chc<V, C>, V> ret = new KBApp<>(e.f, args0);
			return step1(cache, E, R, ret, inhab);
		}
	}
	

	 private Map<V, KBExp<Chc<V, C>, V>> findSubst(KBExp<Chc<V, C>, V> lhs, KBExp<Chc<V, C>, V> e, Map<V,T> lhsCtx, Set<T> allinhab) {
		Map<V, KBExp<Chc<V, C>, V>> s = KBUnifier.findSubst(lhs, e);
		if (s == null || !applies(lhsCtx, s, allinhab)) {
			return null;
		}	
		return s;
	 }
	 
	private KBExp<Chc<V, C>, V> step1(Map<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>> cache, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> E, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> R, KBExp<Chc<V, C>, V> e0, Set<T> inhab) throws InterruptedException {
		KBExp<Chc<V, C>, V> e = e0;
		if (cache != null && cache.containsKey(e)) {
			return cache.get(e);
		}
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r0 : R) {
			checkParentDead();
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r = r0;
			if (!Collections.disjoint(r.first.vars(), e.vars()) || !Collections.disjoint(r.second.vars(), e.vars())) {
				r = freshen(fresh, r0);
			}

			KBExp<Chc<V, C>, V> lhs = r.first;
			KBExp<Chc<V, C>, V> rhs = r.second;
			Map<V, KBExp<Chc<V, C>, V>> s = null;

			s = findSubst(lhs, e, r.third, inhab);
//			System.out.println("looking for subst from " + lhs + " to " + e + "under inhab " + inhab + " needed ctx " + r.third);

			if (s == null) {
//				System.out.println("none");
				continue;
			}
			
			e = rhs.subst(s);
//			System.out.println("after " + e);
		}
		e = step1Es(E, e, inhab);
		
		return e;
	}

	/*
	 * 
		rule 
		
		v1:t1, ..., vN:tN . ... -> ...
		
		term
		
		v'1:t'1 ..., v'N:t'N . ...
		
		have partial assignment from rule to term
		
		v1 -> e1
		...
		
		consider union of all types for variables that are not assigned.  call this needButDontHave
		
		rule only applies if every such type is inhabited given the terms context and its skolem vars
	 */
	private boolean applies(Map<V, T> ruleCtx, Map<V, KBExp<Chc<V, C>, V>> subst, Set<T> inhab) {
		Set<T> need = new HashSet<>();
		for (V ruleVar : ruleCtx.keySet()) {
			if (!subst.containsKey(ruleVar)) {
				need.add(ruleCtx.get(ruleVar));
			}
		}
		return inhab.containsAll(need);
	}

	private KBExp<Chc<V, C>, V> step1Es(Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> E, KBExp<Chc<V, C>, V> e, Set<T> inhab) throws InterruptedException {
		if (options.unfailing && e.vars().isEmpty()) {
			for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r0 : E) {
				KBExp<Chc<V, C>, V> a = step1EsX(r0, e, inhab);
				if (a != null) {
					e = a;
				}
				KBExp<Chc<V, C>, V> b = step1EsX(r0.reverse12(), e, inhab);
				if (b != null) {
					e = b;
				}
			}
		}
		return e;
	}

	private KBExp<Chc<V, C>, V> step1EsX(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r0, KBExp<Chc<V, C>, V> e, Set<T> inhab) throws InterruptedException {
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r = r0;
		if (!Collections.disjoint(r.first.vars(), e.vars()) || !Collections.disjoint(r.second.vars(), e.vars())) {
			r = freshen(fresh, r0);
		}

		KBExp<Chc<V, C>, V> lhs = r.first;
		KBExp<Chc<V, C>, V> rhs = r.second;

		Map<V, KBExp<Chc<V, C>, V>> s0 = findSubst(lhs, e, r.third, inhab);
		if (s0 == null) {
			return null;
		}
		Map<V, KBExp<Chc<V, C>, V>> s = new HashMap<>(s0);

		KBExp<Chc<V, C>, V> lhs0 = lhs.subst(s);
		KBExp<Chc<V, C>, V> rhs0 = rhs.subst(s);

		Set<V> newvars = new HashSet<>();
		newvars.addAll(lhs0.vars());
		newvars.addAll(rhs0.vars());
		Map<V, KBExp<Chc<V, C>, V>> t = new HashMap<>();
		for (V v : newvars) {
			T ty = r.third.get(v);
			if (ty == null || min.get(ty) == null) {
				throw new RuntimeException("Anomaly, please report");
			}
			t.put(v, new KBApp<>(min.get(ty), Collections.emptyList()));
		}
		lhs0 = lhs0.subst(t);
		rhs0 = rhs0.subst(t);

		if (gt_lpo(lhs0, rhs0)) {
			return rhs0;
		} //TODO go other way here?
		return null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////


	private Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> allcps2(Set<Pair<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>>> seen, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab) throws InterruptedException {
		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> ret = new HashSet<>();

		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> E0 = new HashSet<>(E);
		E0.add(ab);
		E0.add(ab.reverse12());
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ba = ab.reverse12();
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> gd : E0) {
			if (Thread.currentThread().isInterrupted()) {
				throw new InterruptedException();
			}
			Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> s;
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> dg = gd.reverse12();

			if (!seen.contains(new Pair<>(ab, gd))) {
				s = cp(ab, gd);
				ret.addAll(s);
				seen.add(new Pair<>(ab, gd));
			}
			if (!seen.contains(new Pair<>(gd, ab))) {
				s = cp(gd, ab);
				ret.addAll(s);
				seen.add(new Pair<>(gd, ab));
			}
			if (!seen.contains(new Pair<>(ab, dg))) {
				s = cp(ab, dg);
				ret.addAll(s);
				seen.add(new Pair<>(ab, dg));
			}
			if (!seen.contains(new Pair<>(dg, ab))) {
				s = cp(dg, ab);
				ret.addAll(s);
				seen.add(new Pair<>(dg, ab));
			}
			////
			if (!seen.contains(new Pair<>(ba, gd))) {
				s = cp(ba, gd);
				ret.addAll(s);
				seen.add(new Pair<>(ba, gd));
			}
			if (!seen.contains(new Pair<>(gd, ba))) {
				s = cp(gd, ba);
				ret.addAll(s);
				seen.add(new Pair<>(gd, ba));
			}
			if (!seen.contains(new Pair<>(ba, dg))) {
				s = cp(ba, dg);
				ret.addAll(s);
				seen.add(new Pair<>(ba, dg));
			}
			if (!seen.contains(new Pair<>(dg, ba))) {
				s = cp(dg, ba);
				ret.addAll(s);
				seen.add(new Pair<>(dg, ba));
			}
		}

		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> gd : R) {
			Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> s;

			if (!seen.contains(new Pair<>(ab, gd))) {
				s = cp(ab, gd);
				ret.addAll(s);
				seen.add(new Pair<>(ab, gd));
			}
			if (!seen.contains(new Pair<>(gd, ab))) {
				s = cp(gd, ab);
				ret.addAll(s);
				seen.add(new Pair<>(gd, ab));
			}
			////
			if (!seen.contains(new Pair<>(ba, gd))) {
				s = cp(ba, gd);
				ret.addAll(s);
				seen.add(new Pair<>(ba, gd));
			}
			if (!seen.contains(new Pair<>(gd, ba))) {
				s = cp(gd, ba);
				ret.addAll(s);
				seen.add(new Pair<>(gd, ba));
			}
		}
		return ret;
	}

	private Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> allcps(Set<Pair<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>>> seen, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab) throws InterruptedException {
		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> ret = new HashSet<>();
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> gd : R) {
			checkParentDead();
			Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> s;
			if (!seen.contains(new Pair<>(ab, gd))) {
				s = cp(ab, gd);
				ret.addAll(s);
				seen.add(new Pair<>(ab, gd));
			}

			if (!seen.contains(new Pair<>(gd, ab))) {
				s = cp(gd, ab);
				ret.addAll(s);
				seen.add(new Pair<>(gd, ab));
			}
		}
		return ret;
	}

	private Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> cp(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> gd0, Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab0) throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab = freshen(fresh, ab0);
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> gd = freshen(fresh, gd0);

		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, KBExp<Chc<V, C>, V>>>> retX = gd.first.cp(new LinkedList<>(), ab.first, ab.second, gd.first, gd.second);

		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> ret = new HashSet<>();
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, KBExp<Chc<V, C>, V>>> c : retX) {
			// ds !>= gs
			KBExp<Chc<V, C>, V> gs = gd.first.subst(c.third);
			KBExp<Chc<V, C>, V> ds = gd.second.subst(c.third);
			if ((gt_lpo(ds, gs)) || gs.equals(ds)) {
				continue;
			}
			// bs !>= as
			KBExp<Chc<V, C>, V> as = ab.first.subst(c.third);
			KBExp<Chc<V, C>, V> bs = ab.second.subst(c.third);
			if ((gt_lpo(bs, as)) || as.equals(bs)) {
				continue;
			}
			Map<V,T> newCtx = new HashMap<>();
			Util.putAllSafely(newCtx, ab.third);
			Util.putAllSafely(newCtx, gd.third); 
			min(newCtx, c.first, c.second);
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> toAdd = new Triple<>(c.first, c.second, newCtx);
			ret.add(toAdd);
		}

		return ret;
	}

	////////////////////////////////////////////////////////////////////////////
	
	private void min(Map<V, T> ctx, KBExp<Chc<V, C>, V> lhs, KBExp<Chc<V, C>, V> rhs) {
		Iterator<V> it = ctx.keySet().iterator();
		while (it.hasNext()) {
			V v = it.next();
			if (lhs.vars().contains(v) || rhs.vars().contains(v)) {
				continue;
			}
			if (groundInhabited.contains(ctx.get(v))) {
				it.remove();
			}
		}
		
	}

	// simplifies equations
	// can also use E U G with extra checking
	private void simplify() throws InterruptedException {
		Map<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>> cache = new HashMap<>(); 

		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> newE = new LinkedList<>();
		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> newE2 = new HashSet<>(); 
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : E) {
			KBExp<Chc<V, C>, V> lhs_red = red(cache, new LinkedList<>(), R, e.first, e.third, Collections.emptyMap());
			KBExp<Chc<V, C>, V> rhs_red = red(cache, new LinkedList<>(), R, e.second, e.third, Collections.emptyMap());
			if (!lhs_red.equals(rhs_red)) {
				Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> p = new Triple<>(lhs_red, rhs_red, e.third);
				if (!newE2.contains(p)) {
					newE.add(p);
					newE2.add(p);
				}
			}
		}
		E = newE;
	}

	private boolean strongGroundJoinable(KBExp<Chc<V, C>, V> s, KBExp<Chc<V, C>, V> t, Map<V,T> ctx) throws InterruptedException {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> R0 = new LinkedList<>();
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> E0 = new LinkedList<>();
		for (Chc<V, C> f : AC_symbols.keySet()) {
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> lx = AC_symbols.get(f);
			R0.add(lx.get(0));
			E0.addAll(lx.subList(1, 5));
		}

		if (!s.equals(red(null, new LinkedList<>(), R0, s, ctx, Collections.emptyMap()))) {
			return false;
		}
		if (!t.equals(red(null, new LinkedList<>(), R0, t, ctx, Collections.emptyMap()))) {
			return false;
		}
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : E0) {
			Map<V, KBExp<Chc<V, C>, V>> m = subsumes0(new Triple<>(s, t, ctx), e);
			if (m == null) {
				m = subsumes0(new Triple<>(t, s, ctx), e);
			}
			if (m == null) {
				m = subsumes0(new Triple<>(s, t, ctx), e.reverse12());
			}
			if (m == null) {
				m = subsumes0(new Triple<>(s, t, ctx), e.reverse12());
			}
			if (m == null) {
				continue;
			}
			return false;
		}

		KBExp<Chc<V, C>, V> s0 = s.sort(AC_symbols.keySet());
		KBExp<Chc<V, C>, V> t0 = t.sort(AC_symbols.keySet());

		return s0.equals(t0);

	}

	// is not collapse2
	// can also use E U G here
	private void collapseBy(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab) throws InterruptedException {
		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> AB = Collections.singleton(ab);
		Iterator<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> it = R.iterator();
		while (it.hasNext()) {
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> r = it.next();
			if (r.equals(ab)) {
				continue;
			}
			KBExp<Chc<V, C>, V> lhs = red(null, new LinkedList<>(), AB, r.first, r.third, Collections.emptyMap());
			if (!r.first.equals(lhs)) {
				addFront(E, new Triple<>(lhs, r.second, r.third));
				it.remove();
			}
		}
	}

	private Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> reduce(Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> set) throws InterruptedException {
		Set<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> p = new HashSet<>();
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : set) {
			KBExp<Chc<V, C>, V> lhs = red(new HashMap<>(), Util.append(E, G), R, e.first, e.third, Collections.emptyMap());
			KBExp<Chc<V, C>, V> rhs = red(new HashMap<>(), Util.append(E, G), R, e.second, e.third, Collections.emptyMap());
			if (lhs.equals(rhs)) {
				continue;
			}
			p.add(new Triple<>(lhs, rhs, e.third));
		}
		return p;
	}

	// TODO: when filtering for subsumed, can also take G into account
	private boolean step() throws InterruptedException {
		checkParentDead();

		if (checkEmpty()) {
			return true;
		}

		if (options.semantic_ac) {
			filterStrongGroundJoinable();
		}

		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> st = pick(E);

		KBExp<Chc<V, C>, V> s0 = st.first;
		KBExp<Chc<V, C>, V> t0 = st.second;
		KBExp<Chc<V, C>, V> a = null, b = null;
		boolean oriented = false;
		if (gt_lpo(s0, t0)) {
			a = s0;
			b = t0;
			oriented = true;
		} else if (gt_lpo(t0, s0)) {
			a = t0;
			b = s0;
			oriented = true;
		} else if (s0.equals(t0)) {
			remove(E, st);
			return false; // in case x = x coming in
		} else {
			if (options.unfailing) {
				remove(E, st);
				add(E, st); // for sorting, will add to end of list
				a = s0;
				b = t0;
			} else {
				throw new RuntimeException("Unorientable: " + st.first + " = " + st.second);
			}
		}
		Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ab = new Triple<>(a, b, st.third);
		if (oriented) {
			R.add(ab);
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CP = filterSubsumed(allcps(seen, ab));
			addAll(E, CP);
			remove(E, st);
			collapseBy(ab);
		} else {
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> CP = filterSubsumed(allcps(seen, ab));
			CP.addAll(filterSubsumed(allcps(seen, ab.reverse12())));
			CP.addAll(filterSubsumed(allcps2(seen, ab)));
			CP.addAll(filterSubsumed(allcps2(seen, ab.reverse12())));
			addAll(E, CP);
		}

		checkParentDead();

		if (options.compose) {
			compose();
			checkParentDead();
		}

		simplify(); //needed for correctness
		checkParentDead();

		if (options.sort_cps) {
			sortByStrLen(E);
			checkParentDead();
		}

		if (options.filter_subsumed_by_self) {
			E = filterSubsumedBySelf(E);
			checkParentDead();
		}

		return false;
	}

	private void filterStrongGroundJoinable() throws InterruptedException {
		List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> newE = new LinkedList<>(E);
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> st : newE) {
			if (strongGroundJoinable(st.first, st.second, st.third)) {
				remove(E, st);
				add(G, st);
			}
		}
		G = filterSubsumedBySelf(G);
	}

	private Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> pick(List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> l) {
		for (int i = 0; i < l.size(); i++) {
			Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> x = l.get(i);
			if (orientable(x)) {
				return l.get(i);
			}
		}
		return l.get(0);
	}

	boolean orientable(Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e) {
		return (gt_lpo(e.first, e.second) || gt_lpo(e.second, e.first));
	}

	private boolean checkEmpty() throws InterruptedException {
		if (E.isEmpty()) {
			isComplete = true;
			isCompleteGround = true;
			return true;
		}
		if (!allUnorientable()) {
			return false;
		}
		if (allCpsConfluent(false, false) || (options.semantic_ac && allCpsConfluent(false, true))) {
			isComplete = false;
			isCompleteGround = true;
			return true;
		}

		return false;
	}

	private boolean allUnorientable() {
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : E) {
			if (orientable(e)) {
				return false;
			}
		}
		return true;
	}

	private boolean allCpsConfluent(boolean print, boolean ground) throws InterruptedException {
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : E) {
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> set = filterSubsumed(reduce(allcps2(new HashSet<>(), e)));
			if (!allCpsConfluent(print, ground, "equation " + e, set)) {
				return false;
			}
		}
		for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : R) {
			List<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> set = filterSubsumed(reduce(allcps(new HashSet<>(), e)));
			if (!allCpsConfluent(print, ground, "rule" + e, set)) {
				return false;
			}
		}
		return true;
	}

	private boolean allCpsConfluent(boolean print, boolean ground, String s, Collection<Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>>> set) throws InterruptedException {
		outer: for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> e : set) {
			KBExp<Chc<V, C>, V> lhs = red(new HashMap<>(), Util.append(E, G), R, e.first, e.third, Collections.emptyMap());
			KBExp<Chc<V, C>, V> rhs = red(new HashMap<>(), Util.append(E, G), R, e.second, e.third, Collections.emptyMap());
			if (!lhs.equals(rhs)) {
				if (!ground) {
					return false;
				} else {
					for (Triple<KBExp<Chc<V, C>, V>, KBExp<Chc<V, C>, V>, Map<V, T>> ex : G) {
						if (subsumes(new Triple<>(lhs, rhs, e.third), ex) || subsumes(new Triple<>(rhs, lhs, e.third), ex)) {
							continue outer;
						}
					}
					if (options.semantic_ac) {
						if (!lhs.sort(AC_symbols.keySet()).equals(rhs.sort(AC_symbols.keySet()))) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public String toString() { 
		List<String> a = E.stream().map(x -> Util.sep(x.third, ":", " ") + " " + x.first + " = " + x.second).collect(Collectors.toList());
		List<String> b = R.stream().map(x -> Util.sep(x.third, ":", " ") + " " + x.first + " -> " + x.second).collect(Collectors.toList());

		return (Util.sep(a, "\n") + "\n" + Util.sep(b, "\n")).trim();
	}

	private static Comparator<Object> ToStringComparator = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			if (o1.toString().length() > o2.toString().length()) {
				return 1;
			} else if (o1.toString().length() < o2.toString().length()) {
				return -1;
			}
			return o1.toString().compareTo(o2.toString());
		}
	};
	

	@Override
	public boolean eq(Map<V, T> ctx, KBExp<C, V> lhs, KBExp<C, V> rhs) {
		return qnf(lhs, ctx).equals(qnf(rhs, ctx));
	}

	@Override
	public boolean hasNFs() {
		return isComplete;
	}

	private KBExp<Chc<V, C>, V> qnf(KBExp<C, V> e, Map<V,T> ctx) {
		try {
			if (isComplete) {
				return red(null, Util.append(E, G), R, e.inject(), ctx, Collections.emptyMap());
			} else if (isCompleteGround) {
				return red(null, Util.append(E, G), R, e.skolemize(), Collections.emptyMap(), ctx);
			}
			throw new RuntimeException("Anomaly: please report");
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		}
	}

	@Override
	public KBExp<C, V> nf(Map<V, T> ctx, KBExp<C, V> e) {
		return deject(qnf(e, ctx));
	}

	private KBExp<C, V> deject(KBExp<Chc<V, C>, V> e) {
		if (e.isVar) {
			return new KBVar<>(e.getVar().var);
		}
		if (e.getApp().f.left) {
			throw new RuntimeException("Anomaly: skolem term " + e.getApp().f.l + " is escaping in " + e);
		}
		return new KBApp<>(e.getApp().f.r, e.getApp().args.stream().map(this::deject).collect(Collectors.toList()));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////

	//In entropic, if ((a o b) o (c o d)) rewrites to ((a o b) o d), the bug is probably here 
	private boolean gt(Chc<V, C> lhs, Chc<V, C> rhs) {
		if (lhs.equals(rhs)) {
			return false;
		} 
		if (lhs.left && rhs.left) {
			if (min.containsValue(lhs) && min.containsValue(rhs)) { //both minimal
				return lhs.l.toString().compareTo(rhs.l.toString()) > 0;
			} else if (!min.containsValue(lhs) && !min.containsValue(rhs)) { //both maximal
				return lhs.l.toString().compareTo(rhs.l.toString()) > 0;
			} else if (min.containsValue(lhs) && !min.containsValue(rhs)) { //lhs minimal, rhs maximal
				return false;
			} else if (!min.containsValue(lhs) && min.containsValue(rhs)) { //lhs maximal, rhs minimal
				return true;
			}
			throw new RuntimeException("Anomaly: please report");
		} else if (lhs.left && !rhs.left) { 
			if (min.containsValue(lhs)) { //lhs minimal
				return false;
			} else { //lhs maximal
				return true;
			}
		} else if (!lhs.left && rhs.left) { 
			if (min.containsValue(rhs)) { //rhs minimal
				return true;
			} else { //rhs maximal
				return false;
			}
		} 

		//both right
		int i = prec.indexOf(lhs.r);
		int j = prec.indexOf(rhs.r);
		if (i == -1 || j == -1) {
			throw new RuntimeException("Anomaly: please report");
		}
		return i > j;
	};
	
	public boolean gt_lpo(KBExp<Chc<V, C>, V> s, KBExp<Chc<V, C>, V> t) {
		return gt_lpo1(s, t) || gt_lpo2(s, t);
	}

	public boolean gt_lpo1(KBExp<Chc<V, C>, V> s, KBExp<Chc<V, C>, V> t) {
		if (s.isVar) {
			return false;
		} else {
			for (KBExp<Chc<V, C>, V> si : s.getApp().args) {
				if (si.equals(t) || gt_lpo(si, t)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean gt_lpo2(KBExp<Chc<V, C>, V> s, KBExp<Chc<V, C>, V> t) {
		if (s.isVar || t.isVar) {
			return false;
		}
		KBApp<Chc<V, C>, V> S = s.getApp();
		KBApp<Chc<V, C>, V> T = t.getApp();
		for (KBExp<Chc<V, C>, V> ti : T.args) {
			if (!gt_lpo(S, ti)) {
				return false;
			}
		}
		if (S.f.equals(T.f)) {
			return gt_lpo_lex(S.args, T.args);
		} else {
			return gt(S.f, T.f);
		}
	}

	public boolean gt_lpo_lex(List<KBExp<Chc<V, C>, V>> ss, List<KBExp<Chc<V, C>, V>> tt) {
		if (ss.size() != tt.size()) {
			throw new RuntimeException("Anomaly: please report");
		}
		if (ss.isEmpty()) {
			return false;
		}
		KBExp<Chc<V, C>, V> s0 = ss.get(0), t0 = tt.get(0);
		if (gt_lpo(s0, t0)) {
			return true;
		}
		if (!s0.equals(t0)) {
			return false;
		}
		return gt_lpo_lex(ss.subList(1, ss.size()), tt.subList(1, tt.size()));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Constraint satisfaction problem for LPO orientability, used to infer precedences
	// http://www.jaist.ac.jp/~hirokawa/publications/03rta.pdf
	// "Tsukuba Termination Tool" Nao Hirokawa and Aart Middeldorp

	private static <X> Set<DAG<X>> tru() {
		return Util.singSet(new DAG<>());
	}
	
	private static <X> Set<DAG<X>> fals() {
		return Collections.emptySet();
	}
	
	private static <X> Set<DAG<X>> and(Set<DAG<X>> a, Set<DAG<X>> b) {
		Set<DAG<X>> ret = new HashSet<>();
		for (DAG<X> x : a) {
			for (DAG<X> y : b) {
				DAG<X> xy = union(x, y);
				if (xy != null) {
					ret.add(xy);
				}
			}
		}

		return min(ret);
	}
	

	private static <X> DAG<X> union(DAG<X> a, DAG<X> b) {
		DAG<X> ret = new DAG<>();
		Set<X> xs = Util.union(a.vertices(), b.vertices());
		for (X x1 : xs) {
			for (X x2 : xs) {
				if (x1.equals(x2)) {
					continue;
				}
				if (a.hasPath(x1, x2) || b.hasPath(x1, x2)) {
					boolean added = ret.addEdge(x1, x2);
					if (!added) {
						return null;
					}
				}
			}
		}
		return ret;
	}
	
	private static <X> Set<DAG<X>> or(Set<DAG<X>> a, Set<DAG<X>> b) {
		return min(Util.union(a, b));
	}
	
	private static <X> Set<DAG<X>> min(Set<DAG<X>> a) { 
		Set<DAG<X>> ret = new HashSet<>();
		for (DAG<X> x : a) {
			if (minimal(x, a)) {
				ret.add(x);
			}
		}
		return ret;
	}
		
	private static <X> boolean minimal(DAG<X> x, Set<DAG<X>> a) {
		for (DAG<X> aa : a) {
			if (lessThanOrEqual(aa, x) && !x.equals(aa)) {
				return false;
			}
		}
		return true;
	}

	private static <X> boolean lessThanOrEqual(DAG<X> a, DAG<X> b) {
		Set<X> xs = Util.union(a.vertices(), b.vertices());
		for (X x1 : xs) {
			for (X x2 : xs) {
				if (a.hasPath(x1, x2) && !b.hasPath(x1, x2)) {
					return false;
				}
			}
		}
		return true;
	}

	 private static <X,V> Set<DAG<X>> eq(KBExp<X, V> s, KBExp<X, V> t) {
		if (s.equals(t)) {
			return tru();
		}
		return fals();
	} 
	
	private static <X> Set<DAG<X>> gtInfer(X lhs, X rhs) {
		if (lhs.equals(rhs)) {
			throw new RuntimeException("Anomaly: please report");
		}
		DAG<X> d = new DAG<>();
		d.addEdge(lhs, rhs);
		return Util.singSet(d);
	} 

	public static <X,V> Set<DAG<X>> gt_lpoInfer(KBExp<X, V> s, KBExp<X, V> t) throws InterruptedException {
		return or(gt_lpo1Infer(s, t), gt_lpo2Infer(s, t)); 
	} 

	public static <X,V> Set<DAG<X>> gt_lpo1Infer(KBExp<X, V> s, KBExp<X, V> t) throws InterruptedException {
		if (s.isVar) {
			return fals();
		} else {
			Set<DAG<X>> ret = fals();
			for (KBExp<X, V> si : s.getApp().args) {
				ret = or(ret, eq(si, t));
				ret = or(ret, gt_lpoInfer(si, t));
			}
			return ret;
		}
	} 

	public static <X,V> Set<DAG<X>> gt_lpo2Infer(KBExp<X, V> s, KBExp<X, V> t) throws InterruptedException {
		if (s.isVar || t.isVar) {
			return fals();
		}
		KBApp<X, V> S = s.getApp();
		KBApp<X, V> T = t.getApp();
	
		Set<DAG<X>> ret = tru();
		for (KBExp<X, V> ti : T.args) {
			ret = and(ret, gt_lpoInfer(S, ti));
		}
		
		Set<DAG<X>> zz = null;
		if (S.f.equals(T.f)) {
			zz = and(ret, gt_lpo_lexInfer(S.args, T.args));
		} else {
			zz = and(ret, gtInfer(S.f, T.f));
		}
		return zz;
	}

	public static <X,V> Set<DAG<X>> gt_lpo_lexInfer(List<KBExp<X, V>> ss, List<KBExp<X, V>> tt) throws InterruptedException {
		if (Thread.currentThread().isInterrupted()) {
			throw new InterruptedException();
		}
		if (ss.size() != tt.size()) {
			throw new RuntimeException("Anomaly: please report");
		}
		if (ss.isEmpty()) {
			return fals();
		}
		KBExp<X, V> s0 = ss.get(0), t0 = tt.get(0);	
		return or(gt_lpoInfer(s0, t0), and(eq(s0,t0), gt_lpo_lexInfer(ss.subList(1, ss.size()), tt.subList(1, tt.size()))));	
	}
	
	public static <C,V,T> List<C> inferPrec(Map<C, Integer> symbols, Set<Triple<KBExp<C, V>, KBExp<C, V>, Map<V,T>>> R0) throws InterruptedException {
		Set<DAG<C>> ret = tru();
		for (Triple<KBExp<C, V>, KBExp<C, V>, Map<V, T>> R : R0) {
			ret = and(ret, gt_lpoInfer(R.first, R.second));
		}
		if (ret.isEmpty()) {
			throw new RuntimeException("There is no LPO precedence that can orient all rules from left to right.  (Unfailing) completion can still be used, but you will have to specify a precedence manually.");
		}
		DAG<C> g = Util.get0X(ret);
		return toPrec(symbols, g); //TODO: just pick one randomly and make it total randomly.   

	}

	//arity-0 < arity-2 < arity-1 < arity-3 < arity-4
	private static <C> List<C> toPrec(Map<C, Integer> cs, DAG<C> g) {
		List<C> ret = new LinkedList<>(g.topologicalSort()); //biggest first
		
		List<C> extra = new LinkedList<>(cs.keySet()); //biggest first
		extra.removeAll(g.vertices());
		Function<Integer, Integer> f = x -> {
			if (x == 2) {
				return 1;
			} else if (x == 1) {
				return 2;
			}
			return x;
		};
		extra.sort((x,y) -> {
			int x0 = cs.get(x);
			int y0 = cs.get(y);
			return Integer.compare(f.apply(x0), f.apply(y0));
		});
		ret.addAll(0, extra);
		return Util.reverse(ret);
	}
	
}
