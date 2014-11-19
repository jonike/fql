package fql_lib.X;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parser.Reference;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Tuple3;
import org.codehaus.jparsec.functors.Tuple4;
import org.codehaus.jparsec.functors.Tuple5;

import fql_lib.Pair;
import fql_lib.Triple;
import fql_lib.X.XExp.Flower;
import fql_lib.X.XExp.XInst;

@SuppressWarnings({"rawtypes", "unchecked"})
public class XParser {

	static final Parser<Integer> NUMBER = Terminals.IntegerLiteral.PARSER
			.map(new org.codehaus.jparsec.functors.Map<String, Integer>() {
				public Integer map(String s) {
					return Integer.valueOf(s);
				}
			});

	static String[] ops = new String[] { ",", ".", ";", ":", "{", "}", "(",
			")", "=", "->", "+", "*", "^", "|", "?" };

	static String[] res = new String[] { "INSTANCE", "as", "flower", "select", "from", "where", "unit", "tt", "pair", "fst", "snd", "void", "ff", "inl", "inr", "case", "relationalize", "return", "coreturn", "variables", "type", "constant", "fn", "assume", "nodes", "edges", "equations", "schema", "mapping", "instance", "homomorphism", "delta", "sigma", "pi" };

	private static final Terminals RESERVED = Terminals.caseSensitive(ops, res);

	public static final Parser<Void> IGNORED = Parsers.or(Scanners.JAVA_LINE_COMMENT,
			Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES).skipMany();

	public static final Parser<?> TOKENIZER = Parsers.or(
			(Parser<?>) Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER,
			RESERVED.tokenizer(), (Parser<?>) Terminals.Identifier.TOKENIZER,
			(Parser<?>) Terminals.IntegerLiteral.TOKENIZER);

	static Parser<?> term(String... names) {
		return RESERVED.token(names);
	}

	public static Parser<?> ident() {
		return string(); //Terminals.Identifier.PARSER;
	}

	public static final Parser<?> program = program().from(TOKENIZER, IGNORED);

	public static final Parser<?> program() {
		return Parsers.tuple(decl().source().peek(), decl()).many();
	}
	
	public static final Parser<?> exp() {
		Reference ref = Parser.newReference();

		Parser<?> sigma = Parsers.tuple(term("sigma"), ref.lazy(), ref.lazy());
		Parser<?> delta = Parsers.tuple(term("delta"), ref.lazy(), ref.lazy());
		Parser<?> pi = Parsers.tuple(term("pi"), ref.lazy(), ref.lazy());
		
		Parser<?> rel = Parsers.tuple(term("relationalize"), ref.lazy());

		Parser<?> coprod = Parsers.tuple(term("("), ref.lazy(), term("+"), ref.lazy(), term(")"));
		Parser<?> inl = Parsers.tuple(term("inl"), ref.lazy(), ref.lazy());
		Parser<?> inr = Parsers.tuple(term("inr"), ref.lazy(), ref.lazy());
		Parser<?> match = Parsers.tuple(term("case"), ref.lazy(), ref.lazy());
		Parser<?> zero = Parsers.tuple(term("void"), ref.lazy());
		Parser<?> ff = Parsers.tuple(term("ff"), ref.lazy());
		
		Parser<?> prod = Parsers.tuple(term("("), ref.lazy(), term("*"), ref.lazy(), term(")"));
		Parser<?> fst = Parsers.tuple(term("fst"), ref.lazy(), ref.lazy());
		Parser<?> snd = Parsers.tuple(term("snd"), ref.lazy(), ref.lazy());
		Parser<?> pair = Parsers.tuple(term("pair"), ref.lazy(), ref.lazy());
		Parser<?> unit = Parsers.tuple(term("unit"), ref.lazy());
		Parser<?> tt = Parsers.tuple(term("tt"), ref.lazy());
		
		Parser<?> ret = Parsers.tuple(term("return"), term("sigma"), term("delta"), ref.lazy(), ref.lazy());
		Parser<?> counit = Parsers.tuple(term("coreturn"), term("sigma"), term("delta"), ref.lazy(), ref.lazy());
		Parser<?> unit1 = Parsers.tuple(term("return"), term("delta"), term("pi"), ref.lazy(), ref.lazy());
		Parser<?> counit1 = Parsers.tuple(term("coreturn"), term("delta"), term("pi"), ref.lazy(), ref.lazy());
		
		Parser<?> flower = flower(ref);
		
		Parser<?> a = Parsers.or(new Parser<?>[] { flower, prod, fst, snd, pair, unit, tt, zero, ff, coprod, inl, inr, match, rel, pi, ret, counit, unit1, counit1, ident(), schema(), mapping(ref), instance(ref), transform(ref), sigma, delta});

		ref.set(a);

		return a;
	}

	public static final Parser<?> type() {
		return Parsers.tuple(term("type"), Parsers.always());
	}
	
	public static final Parser<?> fn() {
		return Parsers.tuple(term("fn"), ident(), term("->"), ident(), Parsers.always());
	}
	
	public static final Parser<?> constx() {
		return Parsers.tuple(term("constant"), ident(), Parsers.always());
	}
	
	public static final Parser<?> assume() {
		return Parsers.tuple(term("assume"), path(), term("="), path());
	}
	
	public static final Parser<?> schema() {
		Parser<?> p1 = ident();
		Parser<?> pX = Parsers.tuple(ident(), term(":"), ident(), term("->"),
				ident());
		Parser<?> p3 = Parsers.tuple(path(), term("="), path());
		Parser<?> foo = Parsers.tuple(section("nodes", p1), 
				section("edges", pX),
				section("equations", p3));
		return Parsers.between(term("schema").followedBy(term("{")), foo, term("}"));
	}
	
	
	public static final XExp.XSchema toCatConst(Object y) {
		List<String> nodes = new LinkedList<>();
		List<Triple<String, String, String>> arrows = new LinkedList<>();
		List<Pair<List<String>, List<String>>> eqs = new LinkedList<>();

		Tuple3 s = (Tuple3) y;

		Tuple3 nodes0 = (Tuple3) s.a;
		Tuple3 arrows0 = (Tuple3) s.b;
		Tuple3 eqs0 = (Tuple3) s.c;

		List nodes1 = (List) nodes0.b;
		List arrows1 = (List) arrows0.b;
		List eqs1 = (List) eqs0.b;

		for (Object o : nodes1) {
			nodes.add((String) o);
		}

		for (Object o : arrows1) {
			Tuple5 x = (Tuple5) o;
			arrows.add(new Triple<>((String) x.a, (String) x.c, (String) x.e));
		}
		for (Object o : eqs1) {
			Tuple3 x = (Tuple3) o;
			List<String> l1 = (List<String>) x.a;
			List<String> l2 = (List<String>) x.c;
			eqs.add(new Pair<>(l1, l2));
		}
		XExp.XSchema c = new XExp.XSchema(nodes, arrows, eqs);
		return c;
	}

	
	public static final Parser<?> decl() {
		Parser e = Parsers.or(new Parser[] { exp(), type(), fn(), constx(), assume() });
		
		Parser p0 = Parsers.tuple(Parsers.tuple(ident(), term(":"), ident()), term("="), exp());
		Parser p1 = Parsers.tuple(ident(), term("="), exp());
		Parser p3 = Parsers.tuple(ident(), term(":"), Parsers.tuple(ident(), term("->"), ident()));
		Parser p4 = Parsers.tuple(ident(), term(":"), term("type"));
		Parser p5 = Parsers.tuple(ident(), term(":"), Parsers.tuple(path(), term("="), path()));
		Parser p2 = Parsers.tuple(ident(), term(":"), ident());
		
		return Parsers.or(new Parser[] {p0, p1, p3, p4, p5, p2});
		
//		return Parsers.tuple(ident(), Parsers.or(term("="), term(":")), e);
	}
	
	public static final Parser<?> instance(Reference ref) {
		Parser<?> node = Parsers.tuple(ident(), term(":"), ident());
		Parser<?> p3 = Parsers.tuple(path(), term("="), path());
		Parser<?> xxx = Parsers.tuple(section("variables", node), 
				section("equations", p3));
		Parser kkk = ((Parser)term("INSTANCE")).or((Parser) term("instance"));
		Parser<?> constant = Parsers
				.tuple(kkk, xxx.between(term("{"), term("}")), term(":"),
						ref.lazy());
		return constant;
	} 
	
	public static final Parser<?> mapping(Reference ref) {
		Parser<?> node = Parsers.tuple(ident(), term("->"), ident());
		Parser<?> arrow = Parsers.tuple(
				ident(),
				term("->"),
				path());

		Parser<?> xxx = Parsers.tuple(section("nodes", node), 
				section("edges", arrow));
		Parser<?> constant = Parsers
				.tuple(Parsers.between(term("mapping").followedBy(term("{")), xxx, term("}")), term(":"),
						ref.lazy(), term("->"), ref.lazy());
		return constant;
	} 
	
	public static final Parser<?> transform(Reference ref) {
		Parser p = Parsers.tuple(ident(), term(":"), ident());
		Parser<?> node = Parsers.tuple(p.or(ident()), term("->"), path());
		Parser<?> xxx =section("variables", node);
		Parser<?> constant = Parsers
				.tuple(Parsers.between(term("homomorphism").followedBy(term("{")), xxx, term("}")), term(":"),
						ref.lazy(), term("->"), ref.lazy());
		return constant;
	} 
	
	public static Parser<?> section2(String s, Parser<?> p) {
		return Parsers.tuple(term(s), p, term(";"));
	}
		
	
/*
	
//	@SuppressWarnings("rawtypes")
	public static FunctorExp toInstConst(Object decl) {
		Tuple3 y = (Tuple3) decl;
		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.a;
		
		Tuple3 nodes = (Tuple3) x.a;
		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
		List arrows0 = (List) arrows.b;


		Map<String, SetExp> nodesX = new HashMap<>();
		for (Object o : nodes0) {
			if (nodesX.containsKey(o)) {
				throw new RuntimeException("Duplicate object: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			SetExp l = toSet(u.c);
			nodesX.put(n, l);
		}
		
		Map<String, Chc<FnExp,SetExp>> arrowsX = new HashMap<>();
		for (Object o : arrows0) {
			if (arrowsX.containsKey(o)) {
				throw new RuntimeException("Duplicate arrow: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			try {
				FnExp l = toFn(u.c);
				arrowsX.put(n, Chc.inLeft(l));
			} catch (Exception eee) {
				SetExp l = toSet(u.c);
				arrowsX.put(n, Chc.inRight(l));				
			}
		}
		InstConst ret = new InstConst(toCat(y.c), nodesX, arrowsX);
		return ret;
	}
	*/
	/*
	public static FunctorExp toCatFtrConst(Object decl) {
		Tuple5 y = (Tuple5) decl;
		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.a;
		
		Tuple3 nodes = (Tuple3) x.a;
		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
		List arrows0 = (List) arrows.b;

		Map<String, CatExp> nodesX = new HashMap<>();
		for (Object o : nodes0) {
			if (nodesX.containsKey(o)) {
				throw new RuntimeException("Duplicate object: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			CatExp l = toCat(u.c);
			nodesX.put(n, l);
		}

		Map<String, FunctorExp> arrowsX = new HashMap<>();
		for (Object o : arrows0) {
			if (arrowsX.containsKey(o)) {
				throw new RuntimeException("Duplicate arrow: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			FunctorExp l = toFtr(u.c);
			arrowsX.put(n, l);
		}
		CatConst ret = new CatConst(toCat(y.c), nodesX, arrowsX);
		return ret;
	}
	*/

	/*public static FunctorExp toMapConst(Object decl) {
		Tuple5 y = (Tuple5) decl;
		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.a;
		
		Tuple3 nodes = (Tuple3) x.a;
		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
		List arrows0 = (List) arrows.b;


		Map<String, String> nodesX = new HashMap<>();
		for (Object o : nodes0) {
			if (nodesX.containsKey(o)) {
				throw new RuntimeException("Duplicate object: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			String l = u.c.toString();
			nodesX.put(n, l);
		}
		
		Map<String, Pair<String, List<String>>> arrowsX = new HashMap<>();
		for (Object o : arrows0) {
			if (arrowsX.containsKey(o)) {
				throw new RuntimeException("Duplicate arrow: " + o + " in " + decl);
			}
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			List<String> l = (List<String>) u.c;
			String ll = l.remove(0);
			arrowsX.put(n, new Pair<>(ll, l));
		}
		MapConst ret = new MapConst(toCat(y.c), toCat(y.e), nodesX, arrowsX);
		return ret;
	} */

	public static final XProgram program(String s) {
		List<Triple<String, Integer, XExp>> ret = new LinkedList<>();
		List decls = (List) program.parse(s);

		for (Object d : decls) {
			org.codehaus.jparsec.functors.Pair pr = (org.codehaus.jparsec.functors.Pair) d;
			Tuple3 decl = (Tuple3) pr.b;
			String txt = pr.a.toString();
			int idx = s.indexOf(txt);
			if (idx < 0) {
				throw new RuntimeException();
			}

			//TODO enforce only flowers on RHS
			if (!(decl.a instanceof String)) {
				Tuple3 t = (Tuple3) decl.a;
				Object ooo = toExp(decl.c);
				if (!(ooo instanceof Flower)) {
					throw new RuntimeException("Can only use v:T for flowers");
				}
				Flower f = (Flower) toExp(decl.c);
				f.ty = t.c.toString();
				ret.add(new Triple<>(t.a.toString(), idx, f));				
			} else {
				String name = decl.a.toString();
				if (decl.b.toString().equals(":")) {
					ret.add(new Triple<>(name, idx, newToExp(decl.c)));				
				} else {
					ret.add(new Triple<>(name, idx, toExp(decl.c)));
				}
			}
		}

		return new XProgram(ret); 
	}
	

	private static XExp newToExp(Object c) {
		if (c.toString().equals("type")) {
			return new XExp.XTy("");
		}
		if (c instanceof String) {
			return new XExp.XConst((String)c, "");
		}
		Tuple3 t = (Tuple3) c;
		if (t.b.toString().equals("->")) {
			return new XExp.XFn((String)t.a, (String)t.c, "");
		}
		return new XExp.XEq((List<String>) t.a, (List<String>) t.c);

	}

	private static XExp toExp(Object c) {
		if (c instanceof String) {
			return new XExp.Var((String) c);
		}
		
		try {
			return toCatConst(c);
		} catch (Exception e) { }
		
		try {
			if (c.toString().contains("variables")) {
				return toInstConst(c);
			}
		} catch (Exception e) { }
		
		try {
			return toMapping(c);
		} catch (Exception e) { }
		
		try {
			return toTrans(c);
		} catch (Exception e) { }
		
		if (c instanceof Tuple5) {
			Tuple5 p = (Tuple5) c; 
			if (p.c.toString().equals("+")) {
				return new XExp.XCoprod(toExp(p.b), toExp(p.d));
			}
			if (p.c.toString().equals("*")) {
				return new XExp.XTimes(toExp(p.b), toExp(p.d));
			}
			if (p.a.toString().equals("return") && p.b.toString().equals("sigma")) {
				return new XExp.XUnit("sigma", toExp(p.d), toExp(p.e));
			}
			if (p.a.toString().equals("coreturn") && p.b.toString().equals("sigma")) {
				return new XExp.XCounit("sigma", toExp(p.d), toExp(p.e));
			}
			if (p.a.toString().equals("return") && p.b.toString().equals("delta")) {
				return new XExp.XUnit("pi", toExp(p.d), toExp(p.e));
			}
			if (p.a.toString().equals("coreturn") && p.b.toString().equals("delta")) {
				return new XExp.XCounit("pi", toExp(p.d), toExp(p.e));
			}

			return new XExp.XFn((String) p.b, (String) p.d, (String) p.e);
		}
		if (c instanceof Tuple4) {
			Tuple4 p = (Tuple4) c;
			return new XExp.XEq((List<String>) p.b, (List<String>) p.d);
		} 
		if (c instanceof Tuple3) {
			Tuple3 p = (Tuple3) c;
			if (p.a.toString().equals("flower")) {
				/*
				Parser<?> from0 = Parsers.tuple(ident(), term("as"), ident()).sepBy(term(","));
				Parser<?> from = Parsers.tuple(term("from"), from0, term(";"));
				Parser<?> where0 = Parsers.tuple(path(), term("="), path()).sepBy(term(","));
				Parser<?> where = Parsers.tuple(term("where"), where0, term(";")); 
				Parser<?> select0 = Parsers.tuple(path(), term("as"), ident()).sepBy(term(","));
				Parser<?> select = Parsers.tuple(term("select"), select0, term(";"));
				Parser p = Parsers.tuple(select, from, where);
				return Parsers.tuple(term("flower"), p.between(term("{"), term("}")), self.lazy()); */
		
				XExp I = toExp(p.c);
				Tuple3 q = (Tuple3) p.b;
				
				List s = (List) ((Tuple3)q.a).b; //list of tuple3 of (path, string)
				List f = (List) ((Tuple3)q.b).b; //list of tuple3 of (string, string)
				List w = (List) ((Tuple3)q.c).b; //list of tuple3 of (path, path)
				
				Map<String, List<String>> select = new HashMap<>();
				Map<String, String> from = new HashMap<>();
				List<Pair<List<String>, List<String>>> where = new LinkedList<>();
				
				Set<String> seen = new HashSet<>();
				for (Object o : w) {
					Tuple3 t = (Tuple3) o;
					List lhs = (List) t.a;
					List rhs = (List) t.c;
					where.add(new Pair<>(rhs, lhs));
			//		seen.addAll(rhs);
			//		seen.addAll(lhs);
				}
				for (Object o : s) {
					Tuple3 t = (Tuple3) o;
					List lhs = (List) t.a;
					String rhs = t.c.toString();
					if (seen.contains(rhs)) {
						throw new RuntimeException("Duplicate AS name: " + rhs + " (note: AS names can't be used in the schema either)");
					}
					seen.add(rhs);
				//	seen.addAll(lhs);
					select.put(rhs, lhs);
				}
				for (Object o : f) {
					Tuple3 t = (Tuple3) o;
					String lhs = t.a.toString();
					String rhs = t.c.toString();
					if (seen.contains(rhs)) {
						throw new RuntimeException("Duplicate AS name: " + rhs + " (note: AS names can't be used in the schema either)");
					}
					seen.add(rhs);
				//	seen.add(lhs);
					from.put(rhs, lhs);
				}

				
				return new XExp.Flower(select, from, where, I);				
			}
			
			if (p.a.toString().equals("sigma")) {
				return new XExp.XSigma(toExp(p.b), toExp(p.c));
			}
			if (p.a.toString().equals("delta")) {
				return new XExp.XDelta(toExp(p.b), toExp(p.c));
			}
			if (p.a.toString().equals("pi")) {
				return new XExp.XPi(toExp(p.b), toExp(p.c));
			}
			if (p.a.toString().equals("inl")) {
				return new XExp.XInj(toExp(p.b), toExp(p.c), true);
			}
			if (p.a.toString().equals("inr")) {
				return new XExp.XInj(toExp(p.b), toExp(p.c), false);
			}
			if (p.a.toString().equals("case")) {
				return new XExp.XMatch(toExp(p.b), toExp(p.c));
			}
			if (p.a.toString().equals("fst")) {
				return new XExp.XProj(toExp(p.b), toExp(p.c), true);
			}
			if (p.a.toString().equals("snd")) {
				return new XExp.XProj(toExp(p.b), toExp(p.c), false);
			}
			if (p.a.toString().equals("pair")) {
				return new XExp.XPair(toExp(p.b), toExp(p.c));
			}
			
			return new XExp.XConst((String) p.b, (String) p.c);
		}
		if (c instanceof org.codehaus.jparsec.functors.Pair) {
			org.codehaus.jparsec.functors.Pair p = (org.codehaus.jparsec.functors.Pair) c;
			if (p.a.toString().equals("relationalize")) {
				return new XExp.XRel(toExp(p.b));
			} 
			if (p.a.toString().equals("void")) {
				return new XExp.XVoid(toExp(p.b));
			}
			if (p.a.toString().equals("ff")) {
				return new XExp.XFF(toExp(p.b));
			}
			if (p.a.toString().equals("unit")) {
				return new XExp.XOne(toExp(p.b));
			}
			if (p.a.toString().equals("tt")) {
				return new XExp.XTT(toExp(p.b));
			}
			
			else {
				return new XExp.XTy((String)p.b);
			}
		}
		
		throw new RuntimeException("x: " + c.getClass() + " " + c);
	}
	
	/* public static final Parser<?> instance(Reference ref) {
		Parser<?> node = Parsers.tuple(ident(), term(":"), ident());
		Parser<?> p3 = Parsers.tuple(path(), term("="), path());
		Parser<?> xxx = Parsers.tuple(section("variables", node), 
				section("equations", p3));
		Parser kkk = ((Parser)term("INSTANCE")).or((Parser) term("instance"));
		Parser<?> constant = Parsers
				.tuple(kkk, xxx.between(term("{"), term("}")), term(":"),
						ref.lazy());
		return constant;  */
	public static XExp.XInst toInstConst(Object decl) {
		Tuple4 y = (Tuple4) decl;
		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.b;
		
		Tuple3 nodes = (Tuple3) x.a;
		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
		List arrows0 = (List) arrows.b;

		List<Pair<String, String>> nodesX = new LinkedList<>();
		for (Object o : nodes0) {
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			String l = (String) u.c;
			nodesX.add(new Pair<>(n, l));
		} 
		
		List<Pair<List<String>, List<String>>> eqsX = new LinkedList<>();
		 for (Object o : arrows0) {
			Tuple3 u = (Tuple3) o;
			List<String> n = (List<String>) u.a;
			List<String> m = (List<String>) u.c;
			eqsX.add(new Pair<>((List<String>) n, (List<String>) m));
		 }
		XInst ret = new XInst(toExp(y.d), nodesX, eqsX);
		if (y.a.toString().equals("INSTANCE")) {
			ret.saturated = true;
		}
		return ret;
	}
	
	public static XExp.XMapConst toMapping(Object decl) {
		Tuple5 y = (Tuple5) decl;
		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.a;
		
		Tuple3 nodes = (Tuple3) x.a;
		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
		List arrows0 = (List) arrows.b;

		List<Pair<String, String>> nodesX = new LinkedList<>();
		for (Object o : nodes0) {
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			String l = (String) u.c;
			nodesX.add(new Pair<>(n, l));
		} 
		
		List<Pair<String, List<String>>> eqsX = new LinkedList<>();
		 for (Object o : arrows0) {
			Tuple3 u = (Tuple3) o;
			String n = (String) u.a;
			List<String> m = (List<String>) u.c;
			eqsX.add(new Pair<>(n, (List<String>) m));
		 }
		XExp.XMapConst ret = new XExp.XMapConst(toExp(y.c), toExp(y.e), nodesX, eqsX);
		return ret;
	}
	
	public static XExp.XTransConst toTrans(Object decl) {
		Tuple5 y = (Tuple5) decl;
//		org.codehaus.jparsec.functors.Pair x = (org.codehaus.jparsec.functors.Pair) y.a;
		
		Tuple3 nodes = (Tuple3) y.a;
//		Tuple3 arrows = (Tuple3) x.b;
		
		List nodes0 = (List) nodes.b;
//		List arrows0 = (List) arrows.b;

		List<Pair<Pair<String, String>, List<String>>> eqsX = new LinkedList<>();
		 for (Object o : nodes0) {
			Tuple3 u = (Tuple3) o;
			List<String> m = (List<String>) u.c;

			if (u.a instanceof Tuple3) {
				Tuple3 n = (Tuple3) u.a;
				eqsX.add(new Pair<>(new Pair<>(n.a.toString(), n.c.toString()), (List<String>) m));
			} else {
				String n = (String) u.a;
				eqsX.add(new Pair<>(new Pair<>(n, null), (List<String>) m));
			}

		 }
		XExp.XTransConst ret = new XExp.XTransConst(toExp(y.c), toExp(y.e), eqsX);
		return ret;
	}

	private static Parser path() {
		return  Parsers.or(ident()).sepBy1(term("."));
	}

	public static Parser<?> section(String s, Parser<?> p) {
		return Parsers.tuple(term(s), p.sepBy(term(",")), term(";"));
	}

	 private static Parser<?> string() {
		return Parsers.or(Terminals.StringLiteral.PARSER,
				Terminals.IntegerLiteral.PARSER, Terminals.Identifier.PARSER);
	} 
	 
	public static final Parser<?> flower(Reference self) {
		Parser<?> from0 = Parsers.tuple(ident(), term("as"), ident()).sepBy(term(","));
		Parser<?> from = Parsers.tuple(term("from"), from0, term(";"));

		Parser<?> where0 = Parsers.tuple(path(), term("="), path()).sepBy(term(","));
		Parser<?> where = Parsers.tuple(term("where"), where0, term(";")); 

		Parser<?> select0 = Parsers.tuple(path(), term("as"), ident()).sepBy(term(","));
		Parser<?> select = Parsers.tuple(term("select"), select0, term(";"));

		Parser p = Parsers.tuple(select, from, where);
		Parser ret = Parsers.tuple(term("flower"), p.between(term("{"), term("}")), self.lazy());
		
		return ret;
	}


}