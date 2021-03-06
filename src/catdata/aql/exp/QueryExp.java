package catdata.aql.exp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import catdata.Chc;
import catdata.Ctx;
import catdata.Pair;
import catdata.Triple;
import catdata.Util;
import catdata.aql.AqlOptions;
import catdata.aql.AqlOptions.AqlOption;
import catdata.aql.Collage;
import catdata.aql.Eq;
import catdata.aql.Instance;
import catdata.aql.It;
import catdata.aql.It.ID;
import catdata.aql.Kind;
import catdata.aql.Mapping;
import catdata.aql.Query;
import catdata.aql.Query.Frozen;
import catdata.aql.Term;
import catdata.aql.Transform;
import catdata.aql.Var;
import catdata.aql.exp.SchExp.SchExpLit;
import catdata.aql.fdm.CoEvalInstance;
import catdata.aql.fdm.CoEvalTransform;
import catdata.aql.fdm.DeltaInstance;
import catdata.aql.fdm.DeltaTransform;
import catdata.aql.fdm.InitialAlgebra;
import catdata.aql.fdm.LiteralInstance;
import catdata.aql.fdm.LiteralTransform;

public abstract class QueryExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2>
		extends Exp<Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2>> {

	@Override
	public Kind kind() {
		return Kind.QUERY;
	}

	public abstract Pair<SchExp<Ty, En1, Sym, Fk1, Att1>, SchExp<Ty, En2, Sym, Fk2, Att2>> type(AqlTyping G);

	///////////////////////////////////////////////////////////////////////////////////////////

	public static final class QueryExpId<Ty, En, Sym, Fk, Att> extends QueryExp<Ty, En, Sym, Fk, Att, En, Fk, Att> {
		@Override
		public Map<String, String> options() {
			return Collections.emptyMap();
		}

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return sch.deps();
		}

		public final SchExp<Ty, En, Sym, Fk, Att> sch;

		public QueryExpId(SchExp<Ty, En, Sym, Fk, Att> sch) {
			if (sch == null) {
				throw new RuntimeException("Attempt to create QueryExpId with null schema");
			}
			this.sch = sch;
		}

		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = prime * result + (sch.hashCode());
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
			QueryExpId<?, ?, ?, ?, ?> other = (QueryExpId<?, ?, ?, ?, ?>) obj;
			return sch.equals(other.sch);
		}

		@Override
		public String toString() {
			return "id " + sch;
		}

		@Override
		public Query<Ty, En, Sym, Fk, Att, En, Fk, Att> eval(AqlEnv env) {
			return Query.id(env.defaults, sch.eval(env));
		}

		@Override
		public Pair<SchExp<Ty, En, Sym, Fk, Att>, SchExp<Ty, En, Sym, Fk, Att>> type(AqlTyping G) {
			return new Pair<>(sch, sch);
		}

	}

	////////////////////////////////////////////////////////////

	public static final class QueryExpVar
			extends QueryExp<Object, Object, Object, Object, Object, Object, Object, Object> {
		public final String var;

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return Util.singList(new Pair<>(var, Kind.QUERY));
		}

		@Override
		public Map<String, String> options() {
			return Collections.emptyMap();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Query<Object, Object, Object, Object, Object, Object, Object, Object> eval(AqlEnv env) {
			return env.defs.qs.get(var);
		}

		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = prime * result + ((var == null) ? 0 : var.hashCode());
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
			QueryExpVar other = (QueryExpVar) obj;
			if (var == null) {
				if (other.var != null)
					return false;
			} else if (!var.equals(other.var))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return var;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Pair<SchExp<Object, Object, Object, Object, Object>, SchExp<Object, Object, Object, Object, Object>> type(
				AqlTyping G) {
			if (!G.defs.qs.containsKey(var)) {
				throw new RuntimeException("Not a query: " + var);
			}
			return (Pair<SchExp<Object, Object, Object, Object, Object>, SchExp<Object, Object, Object, Object, Object>>) ((Object) G.defs.qs
					.get(var));
		}

		public QueryExpVar(String var) {
			this.var = var;
		}

	}

	public static final class QueryExpCompose<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2, En3, Fk3, Att3>
			extends QueryExp<Ty, En1, Sym, Fk1, Att1, En3, Fk3, Att3> {

		public final QueryExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> Q1;
		public final QueryExp<Ty, En2, Sym, Fk2, Att2, En3, Fk3, Att3> Q2;
		public final Map<String, String> options;

		@Override
		public Map<String, String> options() {
			return options;
		}

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return Util.union(Q1.deps(), Q2.deps());
		}

		public QueryExpCompose(QueryExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> Q1,
				QueryExp<Ty, En2, Sym, Fk2, Att2, En3, Fk3, Att3> Q2, List<Pair<String, String>> options) {
			Util.assertNotNull(Q1, Q2);
			this.Q1 = Q1;
			this.Q2 = Q2;
			this.options = Util.toMapSafely(options);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((Q1 == null) ? 0 : Q1.hashCode());
			result = prime * result + ((Q2 == null) ? 0 : Q2.hashCode());
			result = prime * result + ((options == null) ? 0 : options.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof QueryExpCompose))
				return false;
			QueryExpCompose other = (QueryExpCompose) obj;
			if (Q1 == null) {
				if (other.Q1 != null)
					return false;
			} else if (!Q1.equals(other.Q1))
				return false;
			if (Q2 == null) {
				if (other.Q2 != null)
					return false;
			} else if (!Q2.equals(other.Q2))
				return false;
			if (options == null) {
				if (other.options != null)
					return false;
			} else if (!options.equals(other.options))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "(" + Q1 + " ; " + Q2 + ")";
		}

		@Override
		public Pair<SchExp<Ty, En1, Sym, Fk1, Att1>, SchExp<Ty, En3, Sym, Fk3, Att3>> type(AqlTyping G) {
			if (!G.eq(Q1.type(G).second, Q2.type(G).first)) { // TODO aql type
																// equality
				throw new RuntimeException("Cannot compose: target of first, " + Q1.type(G).second
						+ " is not the same as source of second, " + Q2.type(G).first);
			}
			return new Pair<>(Q1.type(G).first, Q2.type(G).second);
		}

		public Query<Ty, En1, Sym, Fk1, Att1, En3, Fk3, Att3> eval(AqlEnv env) {
			AqlOptions ops = new AqlOptions(options, null, env.defaults);
			if ((Boolean)ops.getOrDefault(AqlOption.query_compose_use_incomplete)) {
				return eval_incomplete(env);
			}
			
			Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q1 = Q1.eval(env);
			Query<Ty, En2, Sym, Fk2, Att2, En3, Fk3, Att3> q2 = Q2.eval(env);

			Ctx<En3, Triple<Ctx<Var, En1>, Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>>, AqlOptions>> ens = new Ctx<>();
			Ctx<Att3, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> atts = new Ctx<>();
			Ctx<Fk3, Pair<Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>>, Boolean>> fks = new Ctx<>();


			Map<En3, Pair<Ctx<Var, Pair<Var, Var>>, Ctx<Pair<Var, Var>, Var>>> isos = new HashMap<>(); 
		//	int i = 0;
			for (En3 en3 : q2.dst.ens) {
				Pair<Ctx<Var, Pair<Var, Var>>, Ctx<Pair<Var, Var>, Var>> iso = new Pair<>(new Ctx<>(), new Ctx<>());

				Ctx<Var, En1> fr = new Ctx<>();
				Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>> wh = new LinkedList<>();

				for (Entry<Var, En2> v : q2.ens.get(en3).gens.map.entrySet()) {
					Frozen<Ty, En1, Sym, Fk1, Att1> I = q1.ens.get(v.getValue());
					for (Entry<Var, En1> u : I.gens.map.entrySet()) {
						Var newV = new Var("\"(" + v.getKey() + "," + u.getKey() + ")\"");
						iso.first.put(newV, new Pair<>(v.getKey(), u.getKey()));
						iso.second.put(new Pair<>(v.getKey(), u.getKey()), newV);
						fr.put(newV, u.getValue());
					}
					Function<Var, Var> genf = u -> iso.second.get(new Pair<>(v.getKey(), u));
					for (Eq<Ty, En1, Sym, Fk1, Att1, Var, Void> eq : I.eqs) {
						wh.add(new Eq<>(new Ctx<>(), eq.lhs.mapGen(genf), eq.rhs.mapGen(genf)));
					}
				}

				ens.put(en3, new Triple<>(fr, wh, ops));
				isos.put(en3, iso);
			}

			for (En3 en3 : q2.dst.ens) {
				for (Eq<Ty, En2, Sym, Fk2, Att2, Var, Void> eq : q2.ens.get(en3).eqs) {
					Chc<Ty, En2> ty = q2.ens.get(en3).type(eq.lhs);
					if (!ty.left) {
						
						Transform<Ty, En1, Sym, Fk1, Att1, Var, Void, Var, Void, ID, Chc<Void, Pair<ID, Att1>>, ID, Chc<Void, Pair<ID, Att1>>> lhs = q1
								.compose(q1.transP(eq.lhs), ty.r); 
				
						for (Var v : lhs.gens().keySet()) {
							
							Term<Void, En1, Void, Fk1, Void, Var, Void> xl 
							= trans(q1, isos, ty.r, v, eq.lhs.convert(), en3);

							Term<Void, En1, Void, Fk1, Void, Var, Void> xr
							= trans(q1, isos, ty.r, v, eq.rhs.convert(), en3);

							// TODO aql mutates
							ens.get(en3).second.add(
									new Eq<>(new Ctx<>(), xl.convert(), xr.convert())); 
						}
					} else {
						
					//	q1.a
						Term<Ty, En1, Sym, Fk1, Att1, Var, Void> xl 
						= transT(q1, isos, eq.lhs.convert(), en3);

						Term<Ty, En1, Sym, Fk1, Att1, Var, Void> xr
						= transT(q1, isos, eq.rhs.convert(), en3);
						
						ens.get(en3).second.add(
								new Eq<>(new Ctx<>(), xl.convert(), xr.convert())); 
					}
				}
			}

			for (Fk3 fk3 : q2.dst.fks.keySet()) {
				Transform<Ty, En2, Sym, Fk2, Att2, Var, Void, Var, Void, ID, Chc<Void, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> 
				h = q2.fks.get(fk3);

				Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>> g = new Ctx<>();
				for (Entry<Var, En1> v : ens.get(q2.dst.fks.get(fk3).second).first.map.entrySet()) {
					Pair<Var, Var> p = isos.get(q2.dst.fks.get(fk3).second).first.get(v.getKey());
					Term<Void, En2, Void, Fk2, Void, Var, Void> t = h.gens().get(p.first);
					
					Term<Void, En1, Void, Fk1, Void, Var, Void> xl = trans(q1, isos, h.dst().type(t.convert()).r, p.second, t, q2.dst.fks.get(fk3).first);
					
					g.put(v.getKey(), xl);
				}
				fks.put(fk3, new Pair<>(g, (Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe)));
			}
			
			for (Att3 att3 : q2.dst.atts.keySet()) {
				Term<Ty, En2, Sym, Fk2, Att2, Var, Void> 
				h = q2.atts.get(att3);
				
				Term<Ty, En1, Sym, Fk1, Att1, Var, Void> xl 
				= transT(q1, isos, h, q2.dst.atts.get(att3).first);
				
				atts.put(att3, xl);
			}
			

			return new Query<Ty, En1, Sym, Fk1, Att1, En3, Fk3, Att3>(ens, atts, fks, q1.src, q2.dst,
					(Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));

		}

		public Term<Ty, En1, Sym, Fk1, Att1, Var, Void> transT(Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q1,
				Map<En3, Pair<Ctx<Var, Pair<Var, Var>>, Ctx<Pair<Var, Var>, Var>>> iso,
				Term<Ty, En2, Sym, Fk2, Att2, Var, Void> t, En3 en3) {
			if (t.obj != null) {
				return Term.Obj(t.obj, t.ty);
			} else if (t.sym != null) {
				List<Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> l = new LinkedList<>();
				for (Term<Ty, En2, Sym, Fk2, Att2, Var, Void> arg : t.args) {
					l.add(transT(q1, iso, arg, en3));
				}
				return Term.Sym(t.sym, l);
			} else if (t.att != null) {
				En2 en2 = q1.dst.atts.get(t.att).first;
				Var head = Util.get0(q1.atts.get(t.att).gens());
				Term<Void, En1, Void, Fk1, Void, Var, Void> u = trans(q1, iso, en2, head, t.arg.asArgForAtt(), en3);
				return q1.atts.get(t.att).replace(Term.Gen(head), u.convert());
			}
			return Util.anomaly();
		}

		public Term<Void, En1, Void, Fk1, Void, Var, Void> trans(Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q1,
				Map<En3, Pair<Ctx<Var, Pair<Var, Var>>, Ctx<Pair<Var, Var>, Var>>> iso, En2 en2, Var p,
				Term<Void, En2, Void, Fk2, Void, Var, Void> t, En3 en3) {
			Transform<Ty, En1, Sym, Fk1, Att1, Var, Void, Var, Void, ID, Chc<Void, Pair<ID, Att1>>, ID, Chc<Void, Pair<ID, Att1>>> rhs = q1
					.compose(q1.transP(t.convert()), en2);

			Var lhsGen = Util.get0(t.gens());

			Function<Var, Var> genf = u -> {
				return iso.get(en3).second.get(new Pair<>(lhsGen, u));
			};

			Term<Void, En1, Void, Fk1, Void, Var, Void> z = rhs.gens().get(p);
			Term<Void, En1, Void, Fk1, Void, Var, Void> xl = z.mapGen(genf);
			return xl;
		}

		public Query<Ty, En1, Sym, Fk1, Att1, En3, Fk3, Att3> eval_incomplete(AqlEnv env) {
			Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q1 = Q1.eval(env);
			Query<Ty, En2, Sym, Fk2, Att2, En3, Fk3, Att3> q2 = Q2.eval(env);

			Ctx<En3, Triple<Ctx<Var, En1>, Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>>, AqlOptions>> ens = new Ctx<>();
			Ctx<Att3, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> atts = new Ctx<>();
			Ctx<Fk3, Pair<Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>>, Boolean>> fks = new Ctx<>();

			AqlOptions ops = new AqlOptions(options, null, env.defaults);

			Ctx<Ty, LiteralInstance<Ty, En2, Sym, Fk2, Att2, Void, Var, ID, Chc<Var, Pair<ID, Att2>>>> ys = new Ctx<>();

			Ctx<En3, Pair<Map<ID, Integer>, Map<Integer, ID>>> isos = new Ctx<>();
			Var v = new Var("v");
			Ctx<En3, Map<Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>>> surj = new Ctx<>();

			for (Ty ty : q2.dst.typeSide.tys) {
				Collage<Ty, En2, Sym, Fk2, Att2, Void, Var> col = new Collage<>(q2.src.collage());
				col.sks.put(v, ty);

				InitialAlgebra<Ty, En2, Sym, Fk2, Att2, Void, Var, ID> initial = new InitialAlgebra<>(ops, q2.src, col,
						new It(), Object::toString, Object::toString);
				LiteralInstance<Ty, En2, Sym, Fk2, Att2, Void, Var, ID, Chc<Var, Pair<ID, Att2>>> y = new LiteralInstance<>(
						q2.src, col.gens.map, col.sks.map, new HashSet<>(), initial.dp(), initial,
						(Boolean) ops.getOrDefault(AqlOption.require_consistency),
						(Boolean) ops.getOrDefault(AqlOption.allow_java_eqs_unsafe));
				ys.put(ty, y);
			}

			for (En3 en3 : q2.dst.ens) {
				Collage<Ty, En2, Sym, Fk2, Att2, Var, Void> col = q2.ens.get(en3).collage();

				InitialAlgebra<Ty, En2, Sym, Fk2, Att2, Var, Void, ID> initial = new InitialAlgebra<>(ops, q2.src, col,
						new It(), Object::toString, Object::toString);
				LiteralInstance<Ty, En2, Sym, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>> y = new LiteralInstance<>(
						q2.src, col.gens.map, col.sks.map, q2.ens.get(en3).eqs(), initial.dp(), initial,
						(Boolean) ops.getOrDefault(AqlOption.require_consistency),
						(Boolean) ops.getOrDefault(AqlOption.allow_java_eqs_unsafe)); 

				CoEvalInstance<Ty, En1, Sym, Fk1, Att1, Var, Void, En2, Fk2, Att2, ID, Chc<Void, Pair<ID, Att2>>> J = new CoEvalInstance<>(
						q1, y, ops);
			
				Pair<Map<ID, Integer>, Map<Integer, ID>> iso = J.algebra().intifyX(0);
				isos.put(en3, iso);

				Ctx<Var, En1> fr = new Ctx<>();
				Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>> wh = new LinkedList<>();

				for (Pair<Var, ID> id : J.gens().keySet()) {
					Var v2 = new Var(iso.first.get(J.algebra().nf(Term.Gen(id))) + " "
							+ J.algebra().printX(J.algebra().nf(Term.Gen(id))));
					if (!fr.containsKey(v2)) {
						fr.put(v2, J.gens().get(id));
					}
				}

				Map<Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> surjX = new HashMap<>();
				Function<Pair<Var, ID>, Var> genf = x -> new Var(
						iso.first.get(J.algebra().gen(x)) + " " + J.algebra().printX(J.algebra().gen(x)));

				for (Chc<Void, Pair<ID, Att2>> p : J.sks().keySet()) {
					Set<Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Void>> set = new HashSet<>();
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> u = null;

					outer: for (int i = 0; i < (int) ops.getOrDefault(AqlOption.toCoQuery_max_term_size); i++) {
						Set<Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Void>> set2 = J.collage()
								.applyAllSymbolsNotSk(set);
						for (Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Void> s : set2) {
							Chc<Ty, En1> ty1 = J.type(Term.Sk(p));
							Chc<Ty, En1> ty2 = J.type(s.mapGenSk(Function.identity(), Util.voidFn()));

							if (ty1.equals(ty2)) {
								if (J.dp().eq(new Ctx<>(), Term.Sk(p),
										s.mapGenSk(Function.identity(), Util.voidFn()))) {
									u = s.mapGen(genf);
									break outer;
								}
							}
						}
						set.addAll(set2);
					}
					if (u == null) {
						throw new RuntimeException("Mapping possibly not surjective on attributes, in " + en3
								+ " cannot find expression equivalent to " + J.algebra().printY(Chc.inLeft(p))
								+ ", tried:\n\n" + Util.sep(set, "\n") + "\n\nFrozen is\n\n" + J
								+ "\n\nRepresentable is\n\n" + y);
					}

					surjX.put(Term.Sk(p), u);
				}
				surj.put(en3, surjX);

				for (Pair<Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Chc<Void, Pair<ID, Att2>>>> eq : J
						.eqs()) {

					Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> tz = eq.first.mapGen(genf);
					Term tt0 = tz;
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> lhs = tt0.replace(surjX);

					Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> qw = eq.second.mapGen(genf);
					Term qw1 = qw;
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> rhs = qw1.replace(surjX);

					if (!lhs.equals(rhs)) {
						wh.add(new Eq<>(new Ctx<>(), lhs, rhs));
					}
				}
				ens.put(en3, new Triple<>(fr, wh, ops));
			}
			for (Fk3 fk3 : q2.dst.fks.keySet()) {
				CoEvalTransform<Ty, En1, Sym, Fk1, Att1, Var, Void, En2, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> h = new CoEvalTransform<>(
						q1, q2.fks.get(fk3), ops, ops);

				Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>> g = new Ctx<>();
				for (Entry<Var, En1> u : ens.get(q2.dst.fks.get(fk3).second).first.map.entrySet()) {
					Pair<Map<ID, Integer>, Map<Integer, ID>> iso1 = isos.get(q2.dst.fks.get(fk3).first);
					Pair<Map<ID, Integer>, Map<Integer, ID>> iso2 = isos.get(q2.dst.fks.get(fk3).second);
					Integer u0 = Integer.parseInt(u.getKey().var.substring(0, u.getKey().var.indexOf(" ")));
					ID x = iso2.second.get(u0);
					ID y = h.repr(x);

					Function<Pair<Var, ID>, Var> genf = p -> new Var(iso1.first.get(h.dst().algebra().gen(p)) + " "
							+ h.dst().algebra().printX(h.dst().algebra().gen(p)));

					Term<Void, En1, Void, Fk1, Void, Var, Void> tt = h.dst().algebra().repr(y).mapGen(genf);
					g.put(u.getKey(), tt);
				}
				fks.put(fk3, new Pair<>(g, (Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe)));
			}
			for (Att3 att3 : q2.dst.atts.keySet()) {
				Instance<Ty, En2, Sym, Fk2, Att2, Void, Var, ID, Chc<Var, Pair<ID, Att2>>> y0 = ys
						.get(q2.dst.atts.get(att3).second);
				Instance<Ty, En2, Sym, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>> f0 = q2.ens
						.get(q2.dst.atts.get(att3).first);

				Map<Var, Term<Ty, En2, Sym, Fk2, Att2, Var, Void>> w = new HashMap<>();
				w.put(v, q2.atts.get(att3));

				Transform<Ty, En2, Sym, Fk2, Att2, Void, Var, Var, Void, ID, Chc<Var, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> m = new LiteralTransform<>(
						new HashMap<>(), w, y0, f0, (Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));
				CoEvalTransform<Ty, En1, Sym, Fk1, Att1, Void, Var, En2, Fk2, Att2, Var, Void, ID, Chc<Var, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> h = new CoEvalTransform<>(
						q1, m, ops, ops);

				Term<Ty, En1, Sym, Fk1, Att1, Pair<Var, ID>, Chc<Void, Pair<ID, Att2>>> u = h
						.reprT(Chc.inLeft(Chc.inLeft(v)));

				Pair<Map<ID, Integer>, Map<Integer, ID>> iso1 = isos.get(q2.dst.atts.get(att3).first);

				Function<Pair<Var, ID>, Var> genf = p -> new Var(iso1.first.get(h.dst().algebra().gen(p)) + " "
						+ h.dst().algebra().printX(h.dst().algebra().gen(p)));

				Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> tz = u.mapGen(genf);
				Term tt0 = tz;
				Term<Ty, En1, Sym, Fk1, Att1, Var, Void> tt = tt0.replace(surj.get(q2.dst.atts.get(att3).first));

				atts.put(att3, tt);
			}

			return new Query<Ty, En1, Sym, Fk1, Att1, En3, Fk3, Att3>(ens, atts, fks, q1.src, q2.dst,
					(Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));

		}

	}

	public static final class QueryExpDeltaEval<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2>
			extends QueryExp<Ty, En2, Sym, Fk2, Att2, En1, Fk1, Att1> {

		public final MapExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F;
		public final Map<String, String> options;

		@Override
		public Map<String, String> options() {
			return options;
		}

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return F.deps();
		}

		public QueryExpDeltaEval(MapExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F,
				List<Pair<String, String>> options) {
			Util.assertNotNull(F);
			this.F = F;
			this.options = Util.toMapSafely(options);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((F == null) ? 0 : F.hashCode());
			result = prime * result + ((options == null) ? 0 : options.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof QueryExpDeltaEval))
				return false;
			QueryExpDeltaEval other = (QueryExpDeltaEval) obj;
			if (F == null) {
				if (other.F != null)
					return false;
			} else if (!F.equals(other.F))
				return false;
			if (options == null) {
				if (other.options != null)
					return false;
			} else if (!options.equals(other.options))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "toQuery " + F;
		}

		@Override
		public Pair<SchExp<Ty, En2, Sym, Fk2, Att2>, SchExp<Ty, En1, Sym, Fk1, Att1>> type(AqlTyping G) {
			return new Pair<>(F.type(G).second, F.type(G).first);
		}

		@Override
		public Query<Ty, En2, Sym, Fk2, Att2, En1, Fk1, Att1> eval(AqlEnv env) {
			Mapping<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F0 = F.eval(env);

			Ctx<En1, Triple<Ctx<Var, En2>, Collection<Eq<Ty, En2, Sym, Fk2, Att2, Var, Void>>, AqlOptions>> ens = new Ctx<>();
			Ctx<Att1, Term<Ty, En2, Sym, Fk2, Att2, Var, Void>> atts = new Ctx<>();
			Ctx<Fk1, Pair<Ctx<Var, Term<Void, En2, Void, Fk2, Void, Var, Void>>, Boolean>> fks = new Ctx<>();

			AqlOptions ops = new AqlOptions(options, null, env.defaults);

			Var v = new Var("v");
			for (En1 en1 : F0.src.ens) {
				Ctx<Var, En2> fr = new Ctx<>();
				fr.put(v, F0.ens.get(en1));
				ens.put(en1, new Triple<>(fr, new HashSet<>(), ops));
			}
			for (Att1 att1 : F0.src.atts.keySet()) {
				Term<Ty, En2, Sym, Fk2, Att2, Var, Void> h = F0.atts.get(att1).third.mapGen(Util.voidFn());
				Term<Ty, En2, Sym, Fk2, Att2, Var, Void> g = Term.Gen(v);
				Term<Ty, En2, Sym, Fk2, Att2, Var, Void> t = h.subst(Util.singMap0(F0.atts.get(att1).first, g));
				atts.put(att1, t);
			}
			for (Fk1 fk1 : F0.src.fks.keySet()) {
				Ctx<Var, Term<Void, En2, Void, Fk2, Void, Var, Void>> g = new Ctx<>();
				g.put(v, Term.Fks(F0.fks.get(fk1).second, Term.Gen(v)));
				fks.put(fk1, new Pair<>(g, false));
			}

			return new Query<>(ens, atts, fks, F0.dst, F0.src,
					(Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));
		}

	}

	public static final class QueryExpDeltaCoEval<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2>
			extends QueryExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> {

		public final MapExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F;
		public final Map<String, String> options;

		@Override
		public Map<String, String> options() {
			return options;
		}

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return F.deps();
		}

		public QueryExpDeltaCoEval(MapExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F,
				List<Pair<String, String>> options) {
			Util.assertNotNull(F);
			this.F = F;
			this.options = Util.toMapSafely(options);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((F == null) ? 0 : F.hashCode());
			result = prime * result + ((options == null) ? 0 : options.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof QueryExpDeltaCoEval))
				return false;
			QueryExpDeltaCoEval other = (QueryExpDeltaCoEval) obj;
			if (F == null) {
				if (other.F != null)
					return false;
			} else if (!F.equals(other.F))
				return false;
			if (options == null) {
				if (other.options != null)
					return false;
			} else if (!options.equals(other.options))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "toCoQuery " + F;
		}

		@Override
		public Pair<SchExp<Ty, En1, Sym, Fk1, Att1>, SchExp<Ty, En2, Sym, Fk2, Att2>> type(AqlTyping G) {
			return new Pair<>(F.type(G).first, F.type(G).second);
		}

		@Override
		public Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> eval(AqlEnv env) {
			Mapping<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> F0 = F.eval(env);
			AqlOptions ops = new AqlOptions(options, null, env.defaults);

			Ctx<En2, Triple<Ctx<Var, En1>, Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>>, AqlOptions>> ens = new Ctx<>();
			Ctx<Att2, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> atts = new Ctx<>();
			Ctx<Fk2, Pair<Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>>, Boolean>> fks = new Ctx<>();

			Map<En2, LiteralInstance<Ty, En2, Sym, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>>> ys = new HashMap<>();
			Map<En2, DeltaInstance<Ty, En1, Sym, Fk1, Att1, Var, Void, En2, Fk2, Att2, ID, Chc<Void, Pair<ID, Att2>>>> js = new HashMap<>();

			Map<En2, Pair<Map<Pair<En1, ID>, Integer>, Map<Integer, Pair<En1, ID>>>> isos = new HashMap<>();
			Var v = new Var("v");
			Map<En2, Map<Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>>> surj = new HashMap<>();

			for (En2 en2 : F0.dst.ens) {
				Collage<Ty, En2, Sym, Fk2, Att2, Var, Void> col = new Collage<>(F0.dst.collage());
				col.gens.put(v, en2);

				InitialAlgebra<Ty, En2, Sym, Fk2, Att2, Var, Void, ID> initial = new InitialAlgebra<>(ops, F0.dst, col,
						new It(), Object::toString, Object::toString);
				LiteralInstance<Ty, En2, Sym, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>> y = new LiteralInstance<>(
						F0.dst, col.gens.map, col.sks.map, new HashSet<>(), initial.dp(), initial,
						(Boolean) ops.getOrDefault(AqlOption.require_consistency),
						(Boolean) ops.getOrDefault(AqlOption.allow_java_eqs_unsafe));
				ys.put(en2, y);

				DeltaInstance<Ty, En1, Sym, Fk1, Att1, Var, Void, En2, Fk2, Att2, ID, Chc<Void, Pair<ID, Att2>>> J = new DeltaInstance<>(
						F0, y);
				js.put(en2, J);

				Pair<Map<Pair<En1, ID>, Integer>, Map<Integer, Pair<En1, ID>>> iso = J.algebra().intifyX(0);
				isos.put(en2, iso);

				Ctx<Var, En1> fr = new Ctx<>();
				Collection<Eq<Ty, En1, Sym, Fk1, Att1, Var, Void>> wh = new LinkedList<>();

				for (En1 en1 : J.schema().ens) {
					for (Pair<En1, ID> id : J.algebra().en(en1)) {
						fr.put(new Var(iso.first.get(id) + " " + J.algebra().printX(id)), en1);
					}
				}

				Map<Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Var, Void>> surjX = new HashMap<>();
				for (Chc<Void, Pair<ID, Att2>> p : J.algebra().talg().sks.keySet()) {
					Set<Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Void>> set = new HashSet<>();
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> u = null;

					outer: for (int i = 0; i < (int) ops.getOrDefault(AqlOption.toCoQuery_max_term_size); i++) {
						Set<Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Void>> set2 = J.collage()
								.applyAllSymbolsNotSk(set);
						for (Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Void> s : set2) {
							if (J.type(Term.Sk(p)).equals(J.type(s.mapGenSk(Function.identity(), Util.voidFn())))) {
								if (J.dp().eq(new Ctx<>(), Term.Sk(p),
										s.mapGenSk(Function.identity(), Util.voidFn()))) {
									u = s.mapGen(pp -> new Var(iso.first.get(pp) + " " + J.algebra().printX(pp)));
									break outer;
								}
							}
						}
						set.addAll(set2);
					}
					if (u == null) {
						throw new RuntimeException("Mapping possibly not surjective on attributes, in " + en2
								+ " cannot find expression equivalent to " + J.algebra().printY(p) + ", tried:\n\n"
								+ Util.sep(set, "\n") + "\n\nFrozen is\n\n" + J + "\n\nRepresentable is\n\n" + y);
					}

					surjX.put(Term.Sk(p), u);
				}
				surj.put(en2, surjX);

				for (Pair<Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Chc<Void, Pair<ID, Att2>>>, Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Chc<Void, Pair<ID, Att2>>>> eq : J
						.eqs()) {
					Function<Pair<En1, ID>, Var> genf = x -> new Var(iso.first.get(x) + " " + J.algebra().printX(x));

					Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> tz = eq.first.mapGen(genf);
					Term tt0 = tz;
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> tt = tt0.replace(surjX);

					Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> qw = eq.second.mapGen(genf);
					Term qw1 = qw;
					Term<Ty, En1, Sym, Fk1, Att1, Var, Void> ttA = qw1.replace(surjX);

					if (!tt.equals(ttA)) {
						wh.add(new Eq<>(new Ctx<>(), tt, ttA));
					}
				}
				ens.put(en2, new Triple<>(fr, wh, ops));

				// Frozen<Ty, En1, Sym, Fk1, Att1> frozen = new Frozen<>(fr, wh,
				// F0.src, ops, (Boolean)
				// ops.get(AqlOption.dont_validate_unsafe));

			}
			for (Fk2 fk2 : F0.dst.fks.keySet()) {
				Map<Var, Term<Void, En2, Void, Fk2, Void, Var, Void>> gens = new HashMap<>();
				gens.put(v, Term.Fk(fk2, Term.Gen(v)));
				LiteralTransform<Ty, En2, Sym, Fk2, Att2, Var, Void, Var, Void, ID, Chc<Void, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> t = new LiteralTransform<>(
						gens, new HashMap<>(), ys.get(F0.dst.fks.get(fk2).second), ys.get(F0.dst.fks.get(fk2).first),
						(Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));

				DeltaTransform<Ty, En1, Sym, Fk1, Att1, Var, Void, En2, Fk2, Att2, Var, Void, ID, Chc<Void, Pair<ID, Att2>>, ID, Chc<Void, Pair<ID, Att2>>> h = new DeltaTransform<>(
						F0, t);

				Ctx<Var, Term<Void, En1, Void, Fk1, Void, Var, Void>> g = new Ctx<>();
				for (Entry<Var, En1> u : ens.get(F0.dst.fks.get(fk2).second).first.map.entrySet()) {
					Pair<Map<Pair<En1, ID>, Integer>, Map<Integer, Pair<En1, ID>>> iso1 = isos
							.get(F0.dst.fks.get(fk2).first);
					Pair<Map<Pair<En1, ID>, Integer>, Map<Integer, Pair<En1, ID>>> iso2 = isos
							.get(F0.dst.fks.get(fk2).second);
					Integer u0 = Integer.parseInt(u.getKey().var.substring(0, u.getKey().var.indexOf(" ")));
					Pair<En1, ID> x = iso2.second.get(u0);
					Pair<En1, ID> y = h.repr(x);

					Function<Pair<En1, ID>, Var> genf = p -> {
						Integer y1 = iso1.first.get(p);
						return new Var(y1 + " " + js.get(F0.dst.fks.get(fk2).first).algebra().printX(p));
					};
					Term<Void, En1, Void, Fk1, Void, Var, Void> tt = h.dst().algebra().repr(y).mapGen(genf);
					g.put(u.getKey(), tt);
				}
				fks.put(fk2, new Pair<>(g, (Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe)));
			}
			for (Att2 att2 : F0.dst.atts.keySet()) {
				Term<Ty, En1, Sym, Fk1, Att1, Var, Void> g = null;

				Term<Ty, Void, Sym, Void, Void, Void, Chc<Void, Pair<ID, Att2>>> t = ys.get(F0.dst.atts.get(att2).first)
						.algebra().intoY(Term.Att(att2, Term.Gen(v)));

				Term<Ty, En1, Sym, Fk1, Att1, Pair<En1, ID>, Chc<Void, Pair<ID, Att2>>> s = js
						.get(F0.dst.atts.get(att2).first).algebra().reprT(t); // TODO
																				// aql
																				// is
																				// this
																				// ok?

				Pair<Map<Pair<En1, ID>, Integer>, Map<Integer, Pair<En1, ID>>> iso1 = isos
						.get(F0.dst.atts.get(att2).first);

				Function<Pair<En1, ID>, Var> genf = p -> {
					Integer y1 = iso1.first.get(p);
					return new Var(y1.toString());
				};
				// Function<Void, Pair<ID, Att2>> skf = vv -> Util.abort(vv);
				Term<Ty, En1, Sym, Fk1, Att1, Var, Chc<Void, Pair<ID, Att2>>> tz = s.mapGen(genf);
				Term tt0 = tz;
				Term<Ty, En1, Sym, Fk1, Att1, Var, Void> tt = tt0.replace(surj.get(F0.dst.atts.get(att2).first));

				atts.put(att2, tt); // t.map(Function.identity(),
									// Function.identity(), Util.voidFn(),
									// Util.voidFn(), Util.voidFn(), x ->
									// Util.anomaly()));
			}

			return new Query<>(ens, atts, fks, F0.src, F0.dst,
					(Boolean) ops.getOrDefault(AqlOption.dont_validate_unsafe));
		}

	}

	public static final class QueryExpLit<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2>
			extends QueryExp<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> {

		// TODO: aql add imports
		@Override
		public Map<String, String> options() {
			return Collections.emptyMap();
		}

		public final Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q;

		@Override
		public Collection<Pair<String, Kind>> deps() {
			return Collections.emptyList();
		}

		public QueryExpLit(Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> q) {
			if (q == null) {
				throw new RuntimeException("Attempt to create MapExpLit with null schema");
			}
			this.q = q;
		}

		@Override
		public Query<Ty, En1, Sym, Fk1, Att1, En2, Fk2, Att2> eval(AqlEnv env) {
			throw new RuntimeException("todo");
		}

		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = prime * result + (q.hashCode());
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
			QueryExpLit<?, ?, ?, ?, ?, ?, ?, ?> other = (QueryExpLit<?, ?, ?, ?, ?, ?, ?, ?>) obj;
			return q.equals(other.q);
		}

		@Override
		public String toString() {
			return "QueryExpLit [q=" + q + "]";
		}

		@Override
		public Pair<SchExp<Ty, En1, Sym, Fk1, Att1>, SchExp<Ty, En2, Sym, Fk2, Att2>> type(AqlTyping G) {
			return new Pair<>(new SchExpLit<>(q.src), new SchExpLit<>(q.dst));
		}

	}

}