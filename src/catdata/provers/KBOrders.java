package catdata.provers;

import java.util.function.Function;

import catdata.Pair;
import catdata.opl.OplKB.NewConst;
import catdata.provers.KBExp.KBApp;
/**
 * 
 * @author Ryan Wisnesky
 *
 * Class for term orderings.  Only contains one, lexicographic path ordering left to right.
 */
public class KBOrders {
	
		
	public static <C, V> Function<Pair<KBExp<C, V>, KBExp<C, V>>, Boolean> lpogt(boolean horn,
			Function<Pair<C, C>, Boolean> gt) {
		
	//	LPO<C,V> check = new LPO<>(gt);
		
		Function<Pair<KBExp<C, V>, KBExp<C, V>>, Boolean> ret = new Function<Pair<KBExp<C, V>, KBExp<C, V>>, Boolean>() {
			@Override
			public Boolean apply(Pair<KBExp<C, V>, KBExp<C, V>> xxx) {
				
				KBExp<C, V> s = xxx.first;
				KBExp<C, V> t = xxx.second;

				//for horn clauses
				if (horn) {
					if (KBHorn.isAtom(s) && !KBHorn.isAtom(t)) {
						return true;
					}
					if (s.equals(KBHorn.fals()) && t.equals(KBHorn.tru())) {
						return true;
					}
					if (t.equals(KBHorn.fals())) {
						return true;
					}
				}
				
				if (!s.isVar && s.getApp().f.equals(new NewConst()) && !t.isVar && t.getApp().f.equals(new NewConst()) || !s.isVar && s.getApp().f.equals(new NewConst())) {
					return false;
				} else if (!t.isVar && t.getApp().f.equals(new NewConst())) {
					return true;
				}
				
				//http://resources.mpi-inf.mpg.de/departments/rg1/teaching/autrea-ss10/script/lecture20.pdf
				
				//LPO1
				if (t.isVar) {
					return !t.equals(s) && s.vars().contains(t.getVar().var);
				}
				if (s.isVar) {
					//: KB will fail on var = const
					//if (DEBUG.debug.opl_david) {
						//if (t.vars().isEmpty()) {
							//return true;
						//}
					//}
					return false;
				} 
			

				//LPO2
				KBApp<C, V> s0 = s.getApp();
				KBApp<C, V> t0 = t.getApp();
				C f = s0.f;
				C g = t0.f;

				//LPO2a
				for (KBExp<C, V> si : s0.args) {
					if (apply(new Pair<>(si, t)) || si.equals(t)) {
						return true;
					}
				}

				//LPO2b
				if (gt.apply(new Pair<>(f, g))) { 
					for (KBExp<C, V> ti : t0.args) {
						if (!apply(new Pair<>(s, ti))) {
							return false;
						}
					}
					return true;
				} 
				
				//LPO2c
				if (f.equals(g)) {
					for (KBExp<C, V> ti : t0.args) {
						if (!apply(new Pair<>(s0, ti))) {
							return false;
						}
					}
					int i = 0;
					for (KBExp<C, V> si : s0.args) {
						if (i > t0.args.size()) {
							return false;
						}
						KBExp<C, V> ti = t0.args.get(i++);
						if (apply(new Pair<>(si, ti))) {
							return true;
						}
						if (apply(new Pair<>(ti, si))) {
							return false;
						}
						if (si.equals(ti)) {
							continue;
						}
						return false;
					}
				}
				return false;
			}
		};
		
		
		
		return x -> {
			Boolean b1 = ret.apply(x);
		//	Boolean b2 = check.gt_lpo(x.first, x.second); not NewConst aware
			/* if (!b1.equals(b2)) {
				throw new RuntimeException("Internal consistency error, report to Ryan: On " + x + " orig " + b1 + " but now " + b2);
			}  */
			return b1;
		};
		
	}

	
}
