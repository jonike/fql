package catdata.aql.exp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import catdata.Chc;
import catdata.Ctx;
import catdata.Pair;
import catdata.Triple;
import catdata.Util;
import catdata.aql.AqlJs;
import catdata.aql.AqlOptions;
import catdata.aql.Collage;
import catdata.aql.Eq;
import catdata.aql.Kind;
import catdata.aql.RawTerm;
import catdata.aql.Term;
import catdata.aql.TypeSide;
import catdata.aql.Var;

//TODO aql quoting (reuse example maker?)
public final class TyExpRaw extends TyExp<String, String> implements Raw {
	// Pair<String, Object>

	private Ctx<String, List<InteriorLabel<Object>>> raw = new Ctx<>();

	public Ctx<String, List<InteriorLabel<Object>>> raw() {
		return raw;
	}

	@Override
	public Collection<Pair<String, Kind>> deps() {
		return imports.stream().map(x -> new Pair<>(x, Kind.TYPESIDE)).collect(Collectors.toSet());
	}

	public final Set<String> imports;
	public final Set<String> types;
	public final Set<Pair<String, Pair<List<String>, String>>> functions;
	public final Set<Triple<List<Pair<String, String>>, RawTerm, RawTerm>> eqs;

	public final Set<Pair<String, String>> java_tys_string;
	public final Set<Pair<String, String>> java_parser_string;
	public final Set<Pair<String, Triple<List<String>, String, String>>> java_fns_string;

	public final Map<String, String> options;
	// private final AqlOptions strat;

	@Override
	public Map<String, String> options() {
		return options;
	}

	private final Set<Triple<Ctx<Var, String>, Term<String, Void, String, Void, Void, Void, Void>, Term<String, Void, String, Void, Void, Void, Void>>> eqs0 = new HashSet<>();

	// typesafe by covariance of read-only collections
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	public TyExpRaw(List<LocStr> imports, List<LocStr> types, List<Pair<LocStr, Pair<List<String>, String>>> functions,
			List<Pair<Integer, Triple<List<Pair<String, String>>, RawTerm, RawTerm>>> eqs,
			List<Pair<LocStr, String>> java_tys_string, List<Pair<LocStr, String>> java_parser_string,
			List<Pair<LocStr, Triple<List<String>, String, String>>> java_fns_string,
			List<Pair<String, String>> options) {
		this.imports = LocStr.set1(imports);
		this.types = LocStr.set1(types);
		this.functions = LocStr.functions1(functions);
		this.eqs = LocStr.eqs1(eqs);
		this.java_tys_string = LocStr.set2(java_tys_string);
		this.java_parser_string = LocStr.set2(java_parser_string);
		this.java_fns_string = LocStr.functions2(java_fns_string);
		this.options = Util.toMapSafely(options);

		col.tys.addAll(this.types);
		col.syms.putAll(Util.toMapSafely(this.functions));
		col.java_tys.putAll(Util.toMapSafely(this.java_tys_string));
		col.tys.addAll(col.java_tys.keySet());
		col.java_parsers.putAll(Util.toMapSafely(this.java_parser_string));
		for (Entry<String, Triple<List<String>, String, String>> kv : Util.toMapSafely(this.java_fns_string)
				.entrySet()) {
			col.syms.put(kv.getKey(), new Pair<>(kv.getValue().first, kv.getValue().second));
			col.java_fns.put(kv.getKey(), kv.getValue().third);
		}

		List<InteriorLabel<Object>> i = InteriorLabel.imports("imports", imports);
		raw.put("imports", i);
		List<InteriorLabel<Object>> t = InteriorLabel.imports("types", types);
		raw.put("types", t);

		List<InteriorLabel<Object>> f = new LinkedList<>();
		for (Pair<LocStr, Pair<List<String>, String>> p : functions) {
			f.add(new InteriorLabel<>("functions", new Triple<>(p.first.str, p.second.first, p.second.second),
					p.first.loc,
					x -> x.first + " : " + Util.sep(x.second, ",") + (x.second.isEmpty() ? "" : " -> ") + x.third)
							.conv());
		}
		raw.put("functions", f);

		List<InteriorLabel<Object>> e = new LinkedList<>();
		for (Pair<Integer, Triple<List<Pair<String, String>>, RawTerm, RawTerm>> p : eqs) {
			e.add(new InteriorLabel<>("equations", p.second, p.first, x -> x.second + " = " + x.third).conv());
		}
		raw.put("equations", e);

		List<InteriorLabel<Object>> jt = new LinkedList<>();
		raw.put("java_types", jt);
		for (Pair<LocStr, String> p : java_tys_string) {
			jt.add(new InteriorLabel<>("java_types", new Pair<>(p.first.str, p.second), p.first.loc,
					x -> x.first + " = " + x.second).conv());
		}

		List<InteriorLabel<Object>> jc = new LinkedList<>();
		for (Pair<LocStr, String> p : java_parser_string) {
			jc.add(new InteriorLabel<>("java_constants", new Pair<>(p.first.str, p.second), p.first.loc,
					x -> x.first + " = " + x.second).conv());
		}
		raw.put("java_constants", jc);

		List<InteriorLabel<Object>> jf = new LinkedList<>();
		raw.put("java_functions", jf);
		for (Pair<LocStr, Triple<List<String>, String, String>> p : java_fns_string) {
			jf.add(new InteriorLabel<>("java_functions", new Triple<>(p.first.str, p.second.first, p.second.second),
					p.first.loc, x -> x.first + " : " + Util.sep(x.second, ",") + " -> " + x.third).conv());
		}

	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((eqs == null) ? 0 : eqs.hashCode());
		result = prime * result + ((functions == null) ? 0 : functions.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + ((java_fns_string == null) ? 0 : java_fns_string.hashCode());
		result = prime * result + ((java_parser_string == null) ? 0 : java_parser_string.hashCode());
		result = prime * result + ((java_tys_string == null) ? 0 : java_tys_string.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((types == null) ? 0 : types.hashCode());
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
		TyExpRaw other = (TyExpRaw) obj;
		if (eqs == null) {
			if (other.eqs != null)
				return false;
		} else if (!eqs.equals(other.eqs))
			return false;
		if (functions == null) {
			if (other.functions != null)
				return false;
		} else if (!functions.equals(other.functions))
			return false;
		if (imports == null) {
			if (other.imports != null)
				return false;
		} else if (!imports.equals(other.imports))
			return false;
		if (java_fns_string == null) {
			if (other.java_fns_string != null)
				return false;
		} else if (!java_fns_string.equals(other.java_fns_string))
			return false;
		if (java_parser_string == null) {
			if (other.java_parser_string != null)
				return false;
		} else if (!java_parser_string.equals(other.java_parser_string))
			return false;
		if (java_tys_string == null) {
			if (other.java_tys_string != null)
				return false;
		} else if (!java_tys_string.equals(other.java_tys_string))
			return false;
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (!options.equals(other.options))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}

	private String toString;

	@Override
	public synchronized String toString() {
		if (toString != null) {
			return toString;
		}
		toString = "";

		if (!imports.isEmpty()) {
			toString += "\timports";
			toString += "\n\t\t" + Util.sep(imports, " ") + "\n";
		}

		if (!types.isEmpty()) {
			toString += "\ttypes";
			toString += "\n\t\t" + Util.sep(Util.alphabetical(types), " ") + "\n";
		}

		List<String> temp = new LinkedList<>();

		Map<Object, Object> m = new HashMap<>();
		temp = new LinkedList<>();
		for (Pair<String, Pair<List<String>, String>> sym : Util.alphabetical(functions)) {
			if (sym.second.first.isEmpty()) {
				m.put(sym.first, sym.second.second);
			}
		}
		Map<Object, Set<Object>> n = Util.revS(m);

		if (!n.isEmpty()) {
			toString += "\tconstants";

			for (Object x : Util.alphabetical(n.keySet())) {
				temp.add(Util.sep(n.get(x), " ") + " : " + x);
			}
			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		if (functions.size() != n.size()) {
			toString += "\tfunctions";
			temp = new LinkedList<>();
			for (Pair<String, Pair<List<String>, String>> sym : Util.alphabetical(functions)) {
				if (!sym.second.first.isEmpty()) {
					temp.add(sym.first + " : " + Util.sep(sym.second.first, ", ") + " -> " + sym.second.second);
				}
			}
			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		if (!eqs.isEmpty()) {
			toString += "\tequations";
			temp = new LinkedList<>();
			for (Triple<List<Pair<String, String>>, RawTerm, RawTerm> sym : Util.alphabetical(eqs)) {
				List<String> vars = Util.proj1(sym.first);
				temp.add("forall " + Util.sep(vars, ", ") + ". " + sym.second + " = " + sym.third);
			}
			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		if (!java_tys_string.isEmpty()) {
			toString += "\tjava_types";
			temp = new LinkedList<>();
			for (Pair<String, String> sym : Util.alphabetical(java_tys_string)) {
				temp.add(sym.first + " = " + Util.quote(sym.second));
			}
			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		if (!java_parser_string.isEmpty()) {
			toString += "\tjava_constants";
			temp = new LinkedList<>();
			for (Pair<String, String> sym : Util.alphabetical(java_parser_string)) {
				temp.add(sym.first + " = " + Util.quote(sym.second));
			}
			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		Function<List<String>, String> fff = x -> x.isEmpty() ? "" : (Util.sep(x, ", ") + " -> ");
		if (!java_fns_string.isEmpty()) {
			toString += "\tjava_functions";
			temp = new LinkedList<>();
			for (Pair<String, Triple<List<String>, String, String>> sym : Util.alphabetical(java_fns_string)) {
				temp.add(sym.first + " : " + fff.apply(sym.second.first) + sym.second.second + " = "
						+ Util.quote(sym.second.third));
			}

			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		if (!options.isEmpty()) {
			toString += "\toptions";
			temp = new LinkedList<>();
			for (Entry<String, String> sym : Util.alphabetical(options.entrySet())) {
				temp.add(sym.getKey() + " = " + sym.getValue());
			}

			toString += "\n\t\t" + Util.sep(temp, "\n\t\t") + "\n";
		}

		return "literal {\n" + toString + "}";
	}

	private final Collage<String, Void, String, Void, Void, Void, Void> col = new Collage<>();

	@Override
	public synchronized TypeSide<String, String> eval(AqlEnv env) {
		// defer equation checking since invokes javascript
		AqlJs<String, String> js = new AqlJs<>(col.syms, col.java_tys, col.java_parsers, col.java_fns);
		
		for (Triple<List<Pair<String, String>>, RawTerm, RawTerm> eq : eqs) {
			try {
				Triple<Ctx<Var, String>, Term<String, Void, String, Void, Void, Void, Void>, Term<String, Void, String, Void, Void, Void, Void>> tr = inferEq(
						col, eq, js);
				col.eqs.add(new Eq<>(tr.first.inLeft(), tr.second, tr.third));
				eqs0.add(tr);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				throw new LocException(find("equations", eq), "In equation " + eq.second + " = " + eq.third + ", " + ex.getMessage());
			}

		}

		AqlOptions strat = new AqlOptions(options, col, env.defaults);

		for (String k : imports) {
			@SuppressWarnings("unchecked")
			TypeSide<String, String> v = env.defs.tys.get(k);
			col.tys.addAll(v.tys);
			col.syms.putAll(v.syms.map);
			col.addEqs(v.eqs);
			col.java_tys.putAll(v.js.java_tys.map);
			col.java_fns.putAll(v.js.java_fns.map);
			col.java_parsers.putAll(v.js.java_parsers.map);
			eqs0.addAll(v.eqs);
		}
		//try {
			TypeSide<String, String> ret = new TypeSide<>(col.tys, col.syms.map, eqs0, col.java_tys.map,
					col.java_parsers.map, col.java_fns.map, strat);

			return ret;
		/* } catch (SecException ex) {
			int loc = find(ex.section, ex.o);
			if (loc == -1) {
				throw ex;
			}
			throw new LocException(loc, ex.msg);
		} */
	}

	
	private static Triple<Ctx<Var, String>, Term<String, Void, String, Void, Void, Void, Void>, Term<String, Void, String, Void, Void, Void, Void>> inferEq(
			Collage<String, Void, String, Void, Void, Void, Void> col,
			Triple<List<Pair<String, String>>, RawTerm, RawTerm> eq, AqlJs<String, String> js) {
		Map<String, Chc<String, Void>> ctx = new HashMap<>();
		for (Pair<String, String> p : eq.first) {
			if (ctx.containsKey(p.first)) {
				throw new RuntimeException("Duplicate variable " + p.first + " in context " + Ctx.toString(eq.first));
			}
			if (p.second != null) {
				ctx.put(p.first, Chc.inLeft(p.second));
			} else {
				ctx.put(p.first, null);
			}
		}
		Triple<Ctx<String, Chc<String, Void>>, Term<String, Void, String, Void, Void, Void, Void>, Term<String, Void, String, Void, Void, Void, Void>> eq0 = RawTerm
				.infer1(ctx, eq.second, eq.third, col, js);

		LinkedHashMap<Var, String> map = new LinkedHashMap<>();
		for (String k : ctx.keySet()) {
			Chc<String, Void> v = eq0.first.get(k);
			if (!v.left) {
				throw new RuntimeException("Anomaly: please report");
			}
			map.put(new Var(k), v.l);
		}

		Ctx<Var, String> ctx2 = new Ctx<>(map);
		Term<String, Void, String, Void, Void, Void, Void> lhs = eq0.second;
		Term<String, Void, String, Void, Void, Void, Void> rhs = eq0.third;

		Triple<Ctx<Var, String>, Term<String, Void, String, Void, Void, Void, Void>, Term<String, Void, String, Void, Void, Void, Void>> tr = new Triple<>(
				ctx2, lhs, rhs);
		return tr;
	}

}