package catdata.aql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import catdata.Chc;
import catdata.Ctx;
import catdata.Pair;
import catdata.Quad;
import catdata.Triple;
import catdata.Util;
import catdata.aql.AqlOptions.AqlOption;
import catdata.graph.DMG;
import catdata.graph.UnionFind;

public class ColimitSchema<N, Ty, En, Sym, Fk, Att> implements Semantics {

	/**
	 * size of underlying schema
	 */
	@Override
	public int size() {
		return schemaStr.size();
	}

	public final TypeSide<Ty, Sym> ty;
	
	public final Ctx<N, Schema<Ty, En, Sym, Fk, Att>> nodes;
	
	//public final Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema;
	
	//public final Ctx<N, Mapping<Ty,En,Sym,Fk,Att,Set<Pair<N,En>>,Pair<N,Fk>,Pair<N,Att>>> mappings;

	//actually final 
	public final Schema<Ty, String, Sym, String, String> schemaStr;
	
	//actually final
	public final Ctx<N, Mapping<Ty,En,Sym,Fk,Att,String,String,String>> mappingsStr;
	

	public ColimitSchema<N, Ty, En, Sym, Fk, Att> renameEntity(String src, String dst, boolean checkJava) {
		if (!schemaStr.ens.contains(src)) {
			throw new RuntimeException(src + " is not an entity in \n" + schemaStr);
		}
		if (schemaStr.ens.contains(dst)) {
			throw new RuntimeException(dst + " is already an entity in \n" + schemaStr);
		}
		Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser = Mapping.id(schemaStr);
		Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser = Mapping.id(schemaStr);
	
		Set<String> ens = new HashSet<>(schemaStr.ens);
		ens.remove(src);
		ens.add(dst);
		Map<String, Pair<String, Ty>> atts = new HashMap<>();
		for (String k : schemaStr.atts.keySet()) {
			Pair<String, Ty> v = schemaStr.atts.get(k);
			String s = v.first.equals(src) ? dst : v.first;
			atts.put(k, new Pair<>(s, v.second));
		}
		Map<String, Pair<String, String>> fks = new HashMap<>();
		for (String k : schemaStr.fks.keySet()) {
			Pair<String, String> v = schemaStr.fks.get(k);
			String s = v.first.equals(src) ? dst : v.first;
			String t = v.second.equals(src) ? dst : v.second;
			fks.put(k, new Pair<>(s, t));
		}
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> eqs = new HashSet<>();
		for (Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> eq : schemaStr.eqs) {
			Pair<Var, String> v = eq.first;
			String t = v.second.equals(src) ? dst : v.second;
			eqs.add(new Triple<>(new Pair<>(v.first, t), eq.second, eq.third));				
		}			
		DP<Ty, String, Sym, String, String, Void, Void> dp = new DP<Ty, String, Sym, String, String, Void, Void>() {
			@Override
			public String toStringProver() {
				return "rename entity of " + schemaStr.dp.toStringProver();
			}
			@Override
			//TODO aql check this
			public boolean eq(Ctx<Var, Chc<Ty, String>> ctx, Term<Ty, String, Sym, String, String, Void, Void> lhs, Term<Ty, String, Sym, String, String, Void, Void> rhs) {
				return schemaStr.dp.eq(ctx.map(v -> v.left ? v : (v.r.equals(dst) ? Chc.inRight(src) : v)), lhs, rhs);
			}
		};
		Schema<Ty, String, Sym, String, String> schemaStr2 
		= new Schema<>(ty, ens, atts, fks, eqs, dp, checkJava); //TODO aql java 
		Map<String, String> ensM = new HashMap<>(); 
		for (String k : schemaStr.ens) {
			ensM.put(k, k.equals(src) ? dst : k);
		}
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM = new HashMap<>();
		for (String k : schemaStr.atts.keySet()) {
			attsM.put(k, new Triple<>(isoToUser.atts.get(k).first, isoToUser.atts.get(k).second.equals(src) ? dst : isoToUser.atts.get(k).second, isoToUser.atts.get(k).third));
		}
		Map<String, Pair<String, List<String>>> fksM = new HashMap<>();
		for (String k : schemaStr.fks.keySet()) {
			fksM.put(k, new Pair<>(isoToUser.fks.get(k).first.equals(src) ? dst : isoToUser.fks.get(k).first, isoToUser.fks.get(k).second));
		}
		isoToUser = new Mapping<>(ensM, attsM, fksM, schemaStr, schemaStr2, checkJava);
		Map<String, String> ensM2 = new HashMap<>(); 
		for (String k : schemaStr2.ens) {
			ensM2.put(k, k.equals(dst) ? src : k);
		}
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM2 = new HashMap<>();
		for (String k : schemaStr2.atts.keySet()) {
			attsM2.put(k, new Triple<>(isoFromUser.atts.get(k).first, isoFromUser.atts.get(k).second.equals(dst) ? src : isoFromUser.atts.get(k).second, isoFromUser.atts.get(k).third));
		}
		Map<String, Pair<String, List<String>>> fksM2 = new HashMap<>();
		for (String k : schemaStr2.fks.keySet()) {
			fksM2.put(k, new Pair<>(isoFromUser.fks.get(k).first.equals(dst) ? src : isoFromUser.fks.get(k).first, isoFromUser.fks.get(k).second));
		}
		isoFromUser = new Mapping<>(ensM2, attsM2, fksM2, schemaStr2, schemaStr, checkJava);
	
		return wrap(isoToUser, isoFromUser);
	}
	
	
	public ColimitSchema<N, Ty, En, Sym, Fk, Att> renameFk(String src, String dst, boolean checkJava) {
		if (!schemaStr.fks.containsKey(src)) {
			throw new RuntimeException(src + " is not a foreign_key in \n" + schemaStr);
		}
		if (schemaStr.fks.containsKey(dst)) {
			throw new RuntimeException(dst + " is already a foreign_key in \n" + schemaStr);
		}
		Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser = Mapping.id(schemaStr);
		Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser = Mapping.id(schemaStr);
		Function<String, String> fun = x -> x.equals(src) ? dst : x;
		Function<String, String> fun2= x -> x.equals(dst) ? src : x;
		
		Map<String, Pair<String, String>> fks = new HashMap<>();
		for (String k : schemaStr.fks.keySet()) {
			fks.put(fun.apply(k), schemaStr.fks.get(k));
		}
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> eqs = new HashSet<>();
		for (Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> eq : schemaStr.eqs) {
			eqs.add(new Triple<>(eq.first, eq.second.mapFk(fun), eq.third.mapFk(fun)));				
		}			
		DP<Ty, String, Sym, String, String, Void, Void> dp = new DP<Ty, String, Sym, String, String, Void, Void>() {
			@Override
			public String toStringProver() {
				return "rename foreign key of " + schemaStr.dp.toStringProver();
			}
			@Override
			public boolean eq(Ctx<Var, Chc<Ty, String>> ctx, Term<Ty, String, Sym, String, String, Void, Void> lhs, Term<Ty, String, Sym, String, String, Void, Void> rhs) {
				return schemaStr.dp.eq(ctx, lhs.mapFk(fun2), rhs.mapFk(fun2));
			}
		};
		Schema<Ty, String, Sym, String, String> schemaStr2 
		= new Schema<>(ty, schemaStr.ens, schemaStr.atts.map, fks, eqs, dp, checkJava); //TODO aql java 
		Map<String, Pair<String, List<String>>> fksM = new HashMap<>();
		for (String k : schemaStr.fks.keySet()) {
			fksM.put(k, new Pair<>(schemaStr.fks.get(k).first, k.equals(src) ? Util.singList(dst) : Util.singList(k)));
		}
		isoToUser = new Mapping<>(isoToUser.ens.map, isoToUser.atts.map, fksM, schemaStr, schemaStr2, checkJava);
		Map<String, Pair<String, List<String>>> fksM2 = new HashMap<>();
		for (String k : schemaStr2.fks.keySet()) {
			fksM2.put(k, new Pair<>(schemaStr2.fks.get(k).first, k.equals(dst) ? Util.singList(src) : Util.singList(k)));
		}
		isoFromUser = new Mapping<>(isoFromUser.ens.map, isoFromUser.atts.map, fksM2, schemaStr2, schemaStr, checkJava);
	
		return wrap(isoToUser, isoFromUser);
	}
	
	public ColimitSchema<N, Ty, En, Sym, Fk, Att> renameAtt(String src, String dst, boolean checkJava) {
		if (!schemaStr.atts.containsKey(src)) {
			throw new RuntimeException(src + " is not an attribute in \n" + schemaStr);
		}
		if (schemaStr.atts.containsKey(dst)) {
			throw new RuntimeException(dst + " is already an attribute in \n" + schemaStr);
		}
		Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser = Mapping.id(schemaStr);
		Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser = Mapping.id(schemaStr);
		Function<String, String> fun = x -> x.equals(src) ? dst : x;
		Function<String, String> fun2= x -> x.equals(dst) ? src : x;
		
		Map<String, Pair<String, Ty>> atts = new HashMap<>();
		for (String k : schemaStr.atts.keySet()) {
			atts.put(fun.apply(k), schemaStr.atts.get(k));
		}
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> eqs = new HashSet<>();
		for (Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> eq : schemaStr.eqs) {
			eqs.add(new Triple<>(eq.first, eq.second.mapAtt(fun), eq.third.mapAtt(fun)));				
		}			
		DP<Ty, String, Sym, String, String, Void, Void> dp = new DP<Ty, String, Sym, String, String, Void, Void>() {
			@Override
			public String toStringProver() {
				return "rename attribute of " + schemaStr.dp.toStringProver();
			}
			@Override
			public boolean eq(Ctx<Var, Chc<Ty, String>> ctx, Term<Ty, String, Sym, String, String, Void, Void> lhs, Term<Ty, String, Sym, String, String, Void, Void> rhs) {
				return schemaStr.dp.eq(ctx, lhs.mapAtt(fun2), rhs.mapAtt(fun2));
			}
		};
		Schema<Ty, String, Sym, String, String> schemaStr2 
		= new Schema<>(ty, schemaStr.ens, atts, schemaStr.fks.map, eqs, dp, checkJava);
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM = new HashMap<>();
		for (String k : schemaStr.atts.keySet()) {
			attsM.put(k, new Triple<>(isoToUser.atts.get(k).first, isoToUser.atts.get(k).second, isoToUser.atts.get(k).third.mapAtt(fun)));
		}
		isoToUser = new Mapping<>(isoToUser.ens.map, attsM, isoToUser.fks.map, schemaStr, schemaStr2, checkJava);
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM2 = new HashMap<>();
		for (String k : schemaStr2.atts.keySet()) {
			Var v = new Var("v");
			attsM2.put(k, new Triple<>(v, schemaStr2.atts.get(k).first, Term.Att(fun2.apply(k), Term.Var(v))));
		}
		isoFromUser = new Mapping<>(isoFromUser.ens.map, attsM2, isoFromUser.fks.map, schemaStr2, schemaStr, checkJava);
		
		return wrap(isoToUser, isoFromUser);
	}
	
	public ColimitSchema<N, Ty, En, Sym, Fk, Att> removeFk(String src, List<String> l, boolean checkJava) {
		Var v = new Var("v");
		Term<Ty, String, Sym, String, String, Void, Void> t = Term.Fks(l, Term.Var(v));
		if (!schemaStr.fks.containsKey(src)) {
			throw new RuntimeException(src + " is not a foreign_key in \n" + schemaStr);
		}
		if (l.contains(src)) {
			throw new RuntimeException("Cannot replace " + src + " with " + Util.sep(l, ".") + " because that path contains " + src);
		}
		String en1 = schemaStr.fks.get(src).first;
		String en2 = schemaStr.fks.get(src).second;
		if (!schemaStr.type(new Pair<>(v, en1), t).equals(Chc.inRight(en2))) {
			throw new RuntimeException("The term " + t + " has type " + schemaStr.type(new Pair<>(v, en1), t).toStringMash() + " and not " + en2 + " as expected.");
		}
		if (!schemaStr.dp.eq(new Ctx<>(v, Chc.inRight(en1)), t, Term.Fk(src, Term.Var(v)))) {
			throw new RuntimeException("The term " + t + " is not provably equal to " + Term.Fk(src, Term.Var(v)));
		}
		Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser = Mapping.id(schemaStr);
		Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser = Mapping.id(schemaStr);
		
		Map<String, Pair<String, String>> fks = new HashMap<>(schemaStr.fks.map);
		fks.remove(src);
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> eqs = new HashSet<>();
		for (Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> eq : schemaStr.eqs) {
			Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> 
			tr = new Triple<>(eq.first, eq.second.replaceHead(Head.Fk(src), Util.singList(v), t), eq.third.replaceHead(Head.Fk(src), Util.singList(v), t));
			if (!tr.second.equals(tr.third) && !eqs.contains(tr)) {
				eqs.add(tr);				
			}
		}			
		DP<Ty, String, Sym, String, String, Void, Void> dp = new DP<Ty, String, Sym, String, String, Void, Void>() {
			@Override
			public String toStringProver() {
				return "remove foreign key of " + schemaStr.dp.toStringProver();
			}
			@Override
			public boolean eq(Ctx<Var, Chc<Ty, String>> ctx, Term<Ty, String, Sym, String, String, Void, Void> lhs, Term<Ty, String, Sym, String, String, Void, Void> rhs) {
				return schemaStr.dp.eq(ctx, lhs, rhs);
			}
		};
		Schema<Ty, String, Sym, String, String> schemaStr2 
		= new Schema<>(ty, schemaStr.ens, schemaStr.atts.map, fks, eqs, dp, checkJava);  
		Map<String, Pair<String, List<String>>> fksM = new HashMap<>(isoToUser.fks.map);
		fksM.put(src, new Pair<>(en1, l));
		isoToUser = new Mapping<>(isoToUser.ens.map, isoToUser.atts.map, fksM, schemaStr, schemaStr2, checkJava);
		Map<String, Pair<String, List<String>>> fksM2 = new HashMap<>(isoFromUser.fks.map);
		fksM2.remove(src);
		isoFromUser = new Mapping<>(isoFromUser.ens.map, isoFromUser.atts.map, fksM2, schemaStr2, schemaStr, checkJava);
	
		return wrap(isoToUser, isoFromUser);
	}
	
	public ColimitSchema<N, Ty, En, Sym, Fk, Att> removeAtt(String src, Var v, Term<Ty, String, Sym, String, String, Void, Void> t, boolean checkJava) {
		if (!schemaStr.atts.containsKey(src)) {
			throw new RuntimeException(src + " is not an attribute in \n" + schemaStr);
		}
		String en1 = schemaStr.atts.get(src).first;
		Ty ty0 = schemaStr.atts.get(src).second;
		if (!schemaStr.type(new Pair<>(v, en1), t).equals(Chc.inLeft(ty0))) {
			throw new RuntimeException("The term " + t + " has type " + schemaStr.type(new Pair<>(v, en1), t).toStringMash() + " and not " + ty0 + " as expected.");
		}
		if (!schemaStr.dp.eq(new Ctx<>(v, Chc.inRight(en1)), t, Term.Att(src, Term.Var(v)))) {
			throw new RuntimeException("The term " + t + " is not provably equal to " + Term.Att(src, Term.Var(v)));
		}
		if (t.contains(Head.Att(src))) {
			throw new RuntimeException("Cannot replace " + src + " with " + t + " because that term contains " + src);
		}
		Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser = Mapping.id(schemaStr);
		Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser = Mapping.id(schemaStr);
		
		Map<String, Pair<String, Ty>> atts = new HashMap<>(schemaStr.atts.map);
		atts.remove(src);
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> eqs = new HashSet<>();
		for (Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> eq : schemaStr.eqs) {
			Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>> 
			tr = new Triple<>(eq.first, eq.second.replaceHead(Head.Att(src), Util.singList(v), t), eq.third.replaceHead(Head.Att(src), Util.singList(v), t));
			if (!tr.second.equals(tr.third) && !eqs.contains(tr)) {
				eqs.add(tr);				
			}
		}			
		DP<Ty, String, Sym, String, String, Void, Void> dp = new DP<Ty, String, Sym, String, String, Void, Void>() {
			@Override
			public String toStringProver() {
				return "remove attribute of " + schemaStr.dp.toStringProver();
			}
			@Override
			public boolean eq(Ctx<Var, Chc<Ty, String>> ctx, Term<Ty, String, Sym, String, String, Void, Void> lhs, Term<Ty, String, Sym, String, String, Void, Void> rhs) {
				return schemaStr.dp.eq(ctx, lhs, rhs);
			}
		};
		Schema<Ty, String, Sym, String, String> schemaStr2 
		= new Schema<>(ty, schemaStr.ens, atts, schemaStr.fks.map, eqs, dp, checkJava);  
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM = new HashMap<>(isoToUser.atts.map);
		attsM.put(src, new Triple<>(v, en1, t));
		isoToUser = new Mapping<>(isoToUser.ens.map, attsM, isoToUser.fks.map, schemaStr, schemaStr2, checkJava);
		Map<String, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> attsM2 = new HashMap<>(isoFromUser.atts.map);
		attsM2.remove(src);
		isoFromUser = new Mapping<>(isoFromUser.ens.map, attsM2, isoFromUser.fks.map,schemaStr2, schemaStr, checkJava);

		return wrap(isoToUser, isoFromUser);
	}
	
	public ColimitSchema<N, Ty, En, Sym, Fk, Att> wrap(		
			Mapping<Ty,String,Sym,String,String,String,String,String> isoToUser, 
			Mapping<Ty,String,Sym,String,String,String,String,String> isoFromUser) {
		if (!isoToUser.src.equals(schemaStr)) {
			throw new RuntimeException("Source of " + isoToUser + " \n, namely " + isoToUser.src + "\ndoes not match canonical colimit, namely " + schemaStr);
		}
		checkIso(isoToUser, isoFromUser);
		Ctx<N, Mapping<Ty,En,Sym,Fk,Att,String,String,String>> newMapping = mappingsStr.map((k,v) -> new Pair<>(k, Mapping.compose(v, isoToUser)));
		return new ColimitSchema<>(ty, nodes, isoToUser.dst, newMapping);
	}
	
	/*public Mapping<Ty,En,Sym,Fk,Att,String,String,String> getMapping(N n) {
		return Mapping.compose(mappingsStr.get(n), isoToUser);
	}*/

	private ColimitSchema(TypeSide<Ty, Sym> ty, Ctx<N, Schema<Ty, En, Sym, Fk, Att>> nodes, /*Schema<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>> schema, Ctx<N, Mapping<Ty, En, Sym, Fk, Att, Set<Pair<N, En>>, Pair<N, Fk>, Pair<N, Att>>> mappings,*/ Schema<Ty, String, Sym, String, String> schemaStr,
			Ctx<N, Mapping<Ty, En, Sym, Fk, Att, String, String, String>> mappingsStr) {
		super();
		this.ty = ty;
		this.nodes = nodes;
		this.schemaStr = schemaStr;
		this.mappingsStr = mappingsStr;
	}

	private static <N,En> String conv1(Set<Pair<N,En>> eqc) {
		List<String> l = eqc.stream().map(ColimitSchema::conv2).collect(Collectors.toList());
		l = Util.alphabetical(l);
		return Util.sep(l, "__");
	}
	
	private static <X,Y> String conv2(Pair<X,Y> p) {
		return p.first + "_" + p.second;
	}
	
	private static <N, Ty, En, Sym, Fk, Att> Term<Ty, String, Sym, String, String, Void, Void> conv3(Term<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>, Void, Void> t) {
		return t.map(Function.identity(), Function.identity(), ColimitSchema::conv2, ColimitSchema::conv2, Function.identity(), Function.identity());
	}
	
	private Chc<Ty, String> conv4(Chc<Ty, Set<Pair<N, En>>> v) {
		if (v.left) {
			return Chc.inLeft(v.l);
		}
		return Chc.inRight(conv1(v.r));
	}
	
	//should be real terms in the String schema
	public ColimitSchema(TypeSide<Ty, Sym> ty, Ctx<N, Schema<Ty, En, Sym, Fk, Att>> nodes, 
			Set<Quad<N,En,N,En>> eqEn, 
			Set<Quad<String,String,RawTerm,RawTerm>> eqTerms,
			AqlOptions options) {
		this.ty = ty;
		this.nodes = nodes;
		
		Set<Pair<N,En>> ens = new HashSet<>();
		for (N n : nodes.keySet()) {
			Schema<Ty, En, Sym, Fk, Att> s = nodes.get(n);
			for (En en : s.ens) {
				ens.add(new Pair<>(n, en));
			}
		}
		UnionFind<Pair<N,En>> uf = new UnionFind<>(ens);
		for (Quad<N, En, N, En> s : eqEn) {
			uf.union(new Pair<>(s.first, s.second), new Pair<>(s.third, s.fourth));
		}
		
		Collage<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>, Void, Void> col = new Collage<>(ty.collage()); 
		Ctx<Pair<N, En>, Set<Pair<N, En>>> eqcs = new Ctx<>(uf.toMap());
		col.ens.addAll(eqcs.values());
		Set<Triple<Pair<Var, Set<Pair<N, En>>>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>>> eqs = new HashSet<>();		
		makeCoprodSchema(col, eqs, eqcs);
	
		DP<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> dp = AqlProver.create(options, col, ty.js);
		
		//TODO aql dont forget to add equations to collage and to schema
		//public final Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema;
		
		//public final Ctx<N, Mapping<Ty,En,Sym,Fk,Att,Set<Pair<N,En>>,Pair<N,Fk>,Pair<N,Att>>> mappings;
		boolean b = ! (Boolean) options.getOrDefault(AqlOption.allow_java_eqs_unsafe);
		
		Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema = new Schema<>(ty, col.ens, col.atts.map, col.fks.map, eqs, dp, b);
		
		Pair<Schema<Ty, String, Sym, String, String>, Ctx<N, Mapping<Ty, En, Sym, Fk, Att, String, String, String>>> 
		x = initialUser(options, col, eqs, eqcs, schema);
		
		Schema<Ty, String, Sym, String, String>
		q = quotient(x.first, eqTerms, options);
		
		schemaStr = q;
		mappingsStr = new Ctx<>();
		for (N n : x.second.keySet()) {
			Mapping<Ty, En, Sym, Fk, Att, String, String, String> f = x.second.get(n);
			
			Mapping<Ty, En, Sym, Fk, Att, String, String, String> g 
			= new Mapping<>(f.ens.map, f.atts.map, f.fks.map, f.src, q, b);
			
			mappingsStr.put(n, g);
		}
	}
	
	private Schema<Ty, String, Sym, String, String> quotient(Schema<Ty, String, Sym, String, String> sch, Set<Quad<String, String, RawTerm, RawTerm>> eqTerms, AqlOptions options) {
		Collage<Ty, String, Sym, String, String, Void, Void> col = new Collage<>(sch.collage()); 
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> 
		eqs0 = new HashSet<>(sch.eqs);

		for (Quad<String, String, RawTerm, RawTerm> eq : eqTerms) {
			Map<String, Chc<Ty, String>> ctx = Util.singMap(eq.first, eq.second == null ? null : Chc.inRight(eq.second));
			
			Triple<Ctx<String,Chc<Ty,String>>,Term<Ty,String,Sym,String,String,Void,Void>,Term<Ty,String,Sym,String,String,Void,Void>> 
			eq0 = RawTerm.infer1(ctx, eq.third, eq.fourth, col, ty.js);
			
			Chc<Ty, String> v = eq0.first.get(eq.first);
			if (v.left) {
				throw new RuntimeException("In " + eq.third + " = " + eq.fourth + ", variable " + eq.first + " has type " + v.l + " which is not an entity");
			}
		
			eqs0.add(new Triple<>(new Pair<>(new Var(eq.first), v.r), eq0.second, eq0.third));
			col.eqs.add(new Eq<>(new Ctx<>(new Var(eq.first), v), eq0.second, eq0.third));
		}
	
		boolean b = ! (Boolean) options.getOrDefault(AqlOption.allow_java_eqs_unsafe);
		DP<Ty,String,Sym,String,String,Void,Void> dp = AqlProver.create(options, col, ty.js);
		Schema<Ty, String, Sym, String, String> ret = new Schema<>(ty, col.ens, col.atts.map, col.fks.map, eqs0, dp, b);
		return ret;
	}


	public <E> ColimitSchema(DMG<N, E> shape, TypeSide<Ty, Sym> ty, Ctx<N, Schema<Ty, En, Sym, Fk, Att>> nodes, Ctx<E, Mapping<Ty, En, Sym, Fk, Att, En, Fk, Att>> edges, AqlOptions options) {
		this.ty = ty;
		this.nodes = nodes;
		
		Collage<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>, Void, Void> col = new Collage<>(ty.collage()); 
		Set<Triple<Pair<Var, Set<Pair<N, En>>>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>>> eqs = new HashSet<>();
		
		Set<Pair<N,En>> ens = new HashSet<>();
		for (N n : shape.nodes) {
			Schema<Ty, En, Sym, Fk, Att> s = nodes.get(n);
			for (En en : s.ens) {
				ens.add(new Pair<>(n, en));
			}
		}
		UnionFind<Pair<N,En>> uf = new UnionFind<>(ens);
		for (E e : shape.edges.keySet()) {
			Mapping<Ty, En, Sym, Fk, Att, En, Fk, Att> s = edges.get(e);
			for (En en : s.src.ens) {
				uf.union(new Pair<>(shape.edges.get(e).first , en),
						 new Pair<>(shape.edges.get(e).second, s.ens.get(en)));
			}
		}
		
		Ctx<Pair<N, En>, Set<Pair<N, En>>> eqcs = new Ctx<>(uf.toMap());
		col.ens.addAll(eqcs.values());
		
		makeCoprodSchema(col, eqs, eqcs);
		
		for (E e : shape.edges.keySet()) {
			Mapping<Ty, En, Sym, Fk, Att, En, Fk, Att> s = edges.get(e);
			N src = shape.edges.get(e).first;
			N dst = shape.edges.get(e).second;
			
			for (Fk fk : s.src.fks.keySet()) {
				Pair<En, List<Fk>> fk2 = s.fks.get(fk);
				Var v = new Var("v");
				Pair<Var, Set<Pair<N, En>>> x = new Pair<>(v, eqcs.get(new Pair<>(dst, fk2.first)));
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				lhs = Term.Fk(new Pair<>(src, fk), Term.Var(v));
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				rhs = Term.Fks(fk2.second.stream().map(z -> new Pair<>(dst, z)).collect(Collectors.toList()), Term.Var(v));
				eqs.add(new Triple<>(x, lhs, rhs));
				col.eqs.add(new Eq<>(new Ctx<>(x).inRight(), lhs, rhs));
			}
			for (Att att : s.src.atts.keySet()) {
				Triple<Var, En, Term<Ty, En, Sym, Fk, Att, Void, Void>> fk2 = s.atts.get(att);
				Pair<Var, Set<Pair<N, En>>> x = new Pair<>(fk2.first, eqcs.get(new Pair<>(dst, fk2.second)));
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				lhs = Term.Att(new Pair<>(src, att), Term.Var(fk2.first));
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				rhs = fk2.third.map(Function.identity(), Function.identity(), z -> new Pair<>(dst, z), z -> new Pair<>(dst, z), Function.identity(), Function.identity());
				eqs.add(new Triple<>(x, lhs, rhs));
				col.eqs.add(new Eq<>(new Ctx<>(x).inRight(), lhs, rhs));
			}
		}
	
		boolean b = ! (Boolean) options.getOrDefault(AqlOption.allow_java_eqs_unsafe);
			
		DP<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> dp = AqlProver.create(options, col, ty.js);
		
		//TODO aql dont forget to add equations to collage and to schema
		//public final Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema;
		
		//public final Ctx<N, Mapping<Ty,En,Sym,Fk,Att,Set<Pair<N,En>>,Pair<N,Fk>,Pair<N,Att>>> mappings;
		Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema = new Schema<>(ty, col.ens, col.atts.map, col.fks.map, eqs, dp, b);
		
		Pair<Schema<Ty, String, Sym, String, String>, Ctx<N, Mapping<Ty, En, Sym, Fk, Att, String, String, String>>> 
		x = initialUser(options, col, eqs, eqcs, schema);
		schemaStr = x.first;
		mappingsStr = x.second;

		//TODO: aql check for collisions
	}


	private Pair<Schema<Ty, String, Sym, String, String>, Ctx<N, Mapping<Ty, En, Sym, Fk, Att, String, String, String>>> initialUser(AqlOptions options, Collage<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> col,
			Set<Triple<Pair<Var, Set<Pair<N, En>>>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>>> eqs, Ctx<Pair<N, En>, Set<Pair<N, En>>> eqcs, Schema<Ty, Set<Pair<N,En>>, Sym, Pair<N,Fk>, Pair<N,Att>> schema) {
		Ctx<N, Mapping<Ty,En,Sym,Fk,Att,Set<Pair<N,En>>,Pair<N,Fk>,Pair<N,Att>>> mappings = new Ctx<>();
		
		for (N n : nodes.keySet()) {
			Map<Att, Triple<Var, Set<Pair<N, En>>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>>> atts = new HashMap<>();
			Map<Fk, Pair<Set<Pair<N, En>>, List<Pair<N, Fk>>>> fks = new HashMap<>();
			Map<En, Set<Pair<N, En>>> ens0 = new HashMap<>();
			
			Schema<Ty, En, Sym, Fk, Att> s = nodes.get(n);
			for (En en : s.ens) {
				ens0.put(en, eqcs.get(new Pair<>(n, en)));
			}
			for (Fk fk : s.fks.keySet()) {
				fks.put(fk, new Pair<>(eqcs.get(new Pair<>(n, s.fks.get(fk).first)), Util.singList(new Pair<>(n, fk))));
			}
			for (Att att : s.atts.keySet()) {
				Var v = new Var("v");
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				t = Term.Att(new Pair<>(n, att), Term.Var(v));
				atts.put(att, new Triple<>(v, eqcs.get(new Pair<>(n, s.atts.get(att).first)), t));
			}
						
			Mapping<Ty,En,Sym,Fk,Att,Set<Pair<N,En>>,Pair<N,Fk>,Pair<N,Att>> m = new Mapping<>(ens0 , atts, fks, nodes.get(n), schema, false); //TODO aql allow as option?
			mappings.put(n, m);
		}
		
		Collage<Ty, String, Sym, String, String, Void, Void> colX = new Collage<>(ty.collage()); 
		
		colX.ens.addAll(col.ens.stream().map(ColimitSchema::conv1).collect(Collectors.toSet()));
		colX.atts.map.putAll(col.atts.map((k,v) -> new Pair<>(conv2(k), new Pair<>(conv1(v.first), v.second))).map);
		colX.fks.putAll(col.fks.map((k,v) -> new Pair<>(conv2(k), new Pair<>(conv1(v.first), conv1(v.second)))).map);
		
		Set<Triple<Pair<Var, String>, Term<Ty, String, Sym, String, String, Void, Void>, Term<Ty, String, Sym, String, String, Void, Void>>> 
		eqsX = eqs.stream().map(t -> new Triple<>(new Pair<>(t.first.first, conv1(t.first.second)), conv3(t.second), conv3(t.third))).collect(Collectors.toSet());
		colX.eqs.addAll(col.eqs.stream().map(t -> new Eq<>(t.ctx.map((k,v) -> new Pair<>(k, conv4(v))), conv3(t.lhs), conv3(t.rhs))).collect(Collectors.toSet()));
		
		//AqlOptions opsX = new AqlOptions(options, colX);
		DP<Ty, String, Sym, String, String, Void, Void> dpX = AqlProver.create(options, colX, ty.js);
	
		Schema<Ty, String, Sym, String, String> schemaStr = new Schema<>(ty, colX.ens, colX.atts.map, colX.fks.map, eqsX, dpX, false);
		
		Ctx<N, Mapping<Ty,En,Sym,Fk,Att,String,String,String>> mappingsStr = new Ctx<>();
		for (N n : mappings.keySet()) {
			mappingsStr.put(n, conv5(schemaStr, mappings.get(n)));
		}
		
		return new Pair<>(schemaStr, mappingsStr);
	}


	private void makeCoprodSchema(Collage<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> col, Set<Triple<Pair<Var, Set<Pair<N, En>>>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>, Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void>>> eqs,
			Ctx<Pair<N, En>, Set<Pair<N, En>>> eqcs) {
		for (N n : nodes.keySet()) {
			Schema<Ty, En, Sym, Fk, Att> s = nodes.get(n);
			for (Att att : s.atts.keySet()) {
				col.atts.put(new Pair<>(n, att), new Pair<>(eqcs.get(new Pair<>(n, s.atts.get(att).first)), s.atts.get(att).second));
			}
			for (Fk fk : s.fks.keySet()) {
				col.fks.put(new Pair<>(n, fk), new Pair<>(eqcs.get(new Pair<>(n, s.fks.get(fk).first)), eqcs.get(new Pair<>(n, s.fks.get(fk).second))));
			}
			for (Triple<Pair<Var, En>, Term<Ty, En, Sym, Fk, Att, Void, Void>, Term<Ty, En, Sym, Fk, Att, Void, Void>> eq : s.eqs) {
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				lhs = eq.second.map(Function.identity(), Function.identity(), z -> new Pair<>(n, z), z -> new Pair<>(n, z), Function.identity(), Function.identity());
				Term<Ty, Set<Pair<N, En>>, Sym, Pair<N, Fk>, Pair<N, Att>, Void, Void> 
				rhs = eq.third.map(Function.identity(), Function.identity(), z -> new Pair<>(n, z), z -> new Pair<>(n, z), Function.identity(), Function.identity());
				Pair<Var, Set<Pair<N, En>>> x = new Pair<>(eq.first.first, eqcs.get(new Pair<>(n, eq.first.second)));
				eqs.add(new Triple<>(x, lhs, rhs));
				col.eqs.add(new Eq<>(new Ctx<>(x).inRight(), lhs, rhs));
			}
		}
	}
	
	private void checkIso(Mapping<Ty, String, Sym, String, String, String, String, String> F, Mapping<Ty, String, Sym, String, String, String, String, String> G) {
		isoOneWay(F, G, " when composing (toUser ; fromUser)");
		isoOneWay(G, F, " when composing (fromUser ; toUser)");
	}

	private void isoOneWay(Mapping<Ty, String, Sym, String, String, String, String, String> F, Mapping<Ty, String, Sym, String, String, String, String, String> G, String str) {
		if (!F.dst.equals(G.src)) {
			throw new RuntimeException("Target of " + F + " \n, namely " + F.dst + "\ndoes not match source of " + G + ", namely " + F.src + "\n" + str);
		}
		Mapping<Ty, String, Sym, String, String, String, String, String> f = Mapping.compose(F, G);
		for (String en : f.src.ens) {
			String en2 = f.ens.get(en);
			if (!en.equals(en2)) {
				throw new RuntimeException(en + " taken to " + en2 + ", rather than itself, " + str);
			}
		}
		for (String fk : f.src.fks.keySet()) {
			Pair<String, List<String>> fk2 = f.fks.get(fk);
			Var v = new Var("v");
			Term<Ty, String, Sym, String, String, Void, Void> t = Term.Fks(fk2.second, Term.Var(v));
			Term<Ty, String, Sym, String, String, Void, Void> s = Term.Fk(fk, Term.Var(v));
			boolean eq = F.src.dp.eq(new Ctx<>(new Pair<>(v, Chc.inRight(fk2.first))), s, t);
			if (!eq) {
				throw new RuntimeException(fk + " taken to " + t + ", which is not provably equal to itself, " + str);
			}
		}
		for (String att : f.src.atts.keySet()) {
			Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>> att2 = f.atts.get(att);
			Var v = att2.first;
			Term<Ty, String, Sym, String, String, Void, Void> t = att2.third; //Term.Fks(att2.second, Term.Var(v));
			Term<Ty, String, Sym, String, String, Void, Void> s = Term.Att(att, Term.Var(v));
			boolean eq = F.src.dp.eq(new Ctx<>(new Pair<>(v, Chc.inRight(att2.second))), s, t);
			if (!eq) {
				throw new RuntimeException(att + " taken to " + t + ", which is not provably equal to itself, " + str);
			}
		}
	}

	private Mapping<Ty, En, Sym, Fk, Att, String, String, String> conv5(Schema<Ty, String, Sym, String, String> schemaStr, Mapping<Ty, En, Sym, Fk, Att, Set<Pair<N, En>>, Pair<N, Fk>, Pair<N, Att>> m) {
		Map<En, String> ens = m.ens.map((k,v) -> new Pair<>(k, conv1(v))).map;
		Map<Att, Triple<Var, String, Term<Ty, String, Sym, String, String, Void, Void>>> 
		atts = m.atts.map((k,v) -> new Pair<>(k, new Triple<>(v.first, conv1(v.second), conv3(v.third)))).map;
		Map<Fk, Pair<String, List<String>>> fks = m.fks.map((k,v) -> new Pair<>(k, new Pair<>(conv1(v.first), 
				v.second.stream().map(ColimitSchema::conv2).collect(Collectors.toList())))).map;
		return new Mapping<>(ens, atts, fks, m.src, schemaStr, false);
	}

	@Override
	public String toString() {
		return schemaStr.toString();

	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((ty == null) ? 0 : ty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColimitSchema<?, ?, ?, ?, ?, ?> other = (ColimitSchema<?, ?, ?, ?, ?, ?>) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		if (ty == null) {
			if (other.ty != null)
				return false;
		} else if (!ty.equals(other.ty))
			return false;
		return true;
	}

	@Override
	public Kind kind() {
		return Kind.SCHEMA_COLIMIT;
	}

//	
	


}