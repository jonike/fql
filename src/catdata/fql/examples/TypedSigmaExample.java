package catdata.fql.examples;

import catdata.ide.Example;
import catdata.ide.Language;


public class TypedSigmaExample extends Example {

	@Override
	public Language lang() {
		return Language.FQL;
	}
	
	@Override
	public String getName() {
		return "Sigma";
	}

	@Override
	public String getText() {
		return text;
	}
	
	private final String text  =
			"schema C = {"
					+ "\n nodes "
					+ "\n	a1, a2, a3, b1, b2, c1, c2, c3, c4;"
					+ "\n"
					+ "\n attributes"
					+ "\n	a1_str : a1 -> string,"
					+ "\n 	a2_str : a2 -> string,"
					+ "\n 	a3_str : a3 -> string,"
					+ "\n	"
					+ "\n	b1_str : b1 -> string,"
					+ "\n 	b2_str : b2 -> string,"
					+ "\n"
					+ "\n 	c1_str : c1 -> string,"
					+ "\n 	c2_str : c2 -> string,"
					+ "\n 	c3_str : c3 -> string,"
					+ "\n 	c4_str : c4 -> string;"
					+ "\n 	"
					+ "\n arrows"
					+ "\n	g1 : a1 -> b1, "
					+ "\n	g2 : a2 -> b2, "
					+ "\n	g3 : a3 -> b2,"
					+ "\n	h1 : a1 -> c1, "
					+ "\n	h2 : a2 -> c2, "
					+ "\n	h3 : a3 -> c4;"
					+ "\n"
					+ "\n equations;"
					+ "\n}"
					+ "\n"
					+ "\nschema D = {"
					+ "\n nodes "
					+ "\n 	A, B, C;"
					+ "\n attributes"
					+ "\n	A_str : A -> string,"
					+ "\n 	B_str : B -> string,"
					+ "\n 	C_str : C -> string;"
					+ "\n arrows"
					+ "\n 	G : A -> B, "
					+ "\n 	H : A -> C;"
					+ "\n equations;"
					+ "\n}"
					+ "\n"
					+ "\nmapping F = {"
					+ "\n nodes "
					+ "\n  	a1 -> A, "
					+ "\n  	a2 -> A, "
					+ "\n  	a3 -> A,"
					+ "\n  	b1 -> B,"
					+ "\n  	b2 -> B,"
					+ "\n  	c1 -> C, "
					+ "\n  	c2 -> C, "
					+ "\n  	c3 -> C, "
					+ "\n  	c4 -> C;"
					+ "\n attributes"
					+ "\n  	a1_str -> A_str, "
					+ "\n  	a2_str -> A_str, "
					+ "\n  	a3_str -> A_str,"
					+ "\n  	b1_str -> B_str, "
					+ "\n  	b2_str -> B_str,"
					+ "\n  	c1_str -> C_str, "
					+ "\n  	c2_str -> C_str,"
					+ "\n  	c3_str -> C_str, "
					+ "\n  	c4_str -> C_str;"
					+ "\n arrows  "
					+ "\n  	g1 -> A.G, "
					+ "\n 	g2 -> A.G, "
					+ "\n  	g3 -> A.G,"
					+ "\n  	h1 -> A.H, "
					+ "\n  	h2 -> A.H, "
					+ "\n  	h3 -> A.H;"
					+ "\n} : C -> D"
					+ "\n"
					+ "\n"
					+ "\ninstance I = {"
					+ "\n nodes"
					+ "\n 	b2 -> {a,b,c},"
					+ "\n 	b1 -> {d,e},"
					+ "\n"
					+ "\n 	a3 -> {1,2},"
					+ "\n 	a2 -> {3,4,5},"
					+ "\n	a1 -> {6},"
					+ "\n "
					+ "\n 	c4 -> {f,g},"
					+ "\n 	c3 -> {h},"
					+ "\n 	c2 -> {i,j},"
					+ "\n 	c1 -> {k,l};"
					+ "\n"
					+ "\n attributes"
					+ "\n	b2_str -> {(a,a),(b,b),(c,c)},"
					+ "\n 	b1_str -> {(d,d),(e,e)}, "
					+ "\n"
					+ "\n 	a1_str -> {(6, 6)},"
					+ "\n 	a2_str -> {(3,3),(4,4),(5,5)},"
					+ "\n 	a3_str -> {(1,1),(2,2)},"
					+ "\n"
					+ "\n 	c4_str -> {(f,f),(g,g)},"
					+ "\n 	c3_str -> {(h,h)},"
					+ "\n 	c2_str -> {(i,i),(j,j)},"
					+ "\n 	c1_str -> {(k,k),(l,l)};"
					+ "\n 	"
					+ "\n arrows"
					+ "\n 	g3 -> {(1,a),(2,b)},"
					+ "\n 	g2 -> {(3,a),(4,b),(5,c)},"
					+ "\n 	g1 -> {(6,d)},"
					+ "\n"
					+ "\n 	h3 -> {(1,f),(2,g)},"
					+ "\n 	h2 -> {(3,i),(4,j),(5,j)},"
					+ "\n 	h1 -> {(6,k)}; "
					+ "\n} : C"
					+ "\n"
					+ "\ninstance J = sigma F I"
					+ "\n"
					+ "\ninstance JX = delta F J"
					+ "\n"
					+ "\ntransform monad_unit = JX.return"
					+ "\n"
					+ "\ninstance IX = sigma F JX"
					+ "\n"
					+ "\ntransform monad_counit = IX.coreturn"
					+ "\n"
					+ "\ninstance IY = delta F IX"
					+ "\n"
					+ "\ntransform monad_join = delta IY JX monad_counit"
					+ "\n"
					+ "\ninstance I0 = {"
					+ "\n nodes"
					+ "\n 	b2 -> {a,b},"
					+ "\n 	b1 -> {d},"
					+ "\n"
					+ "\n 	a3 -> {1},"
					+ "\n 	a2 -> {3,4},"
					+ "\n	a1 -> {},"
					+ "\n "
					+ "\n 	c4 -> {f},"
					+ "\n 	c3 -> {},"
					+ "\n 	c2 -> {i,j},"
					+ "\n 	c1 -> {k};"
					+ "\n"
					+ "\n attributes"
					+ "\n	b2_str -> {(a,a),(b,b)},"
					+ "\n 	b1_str -> {(d,d)}, "
					+ "\n"
					+ "\n 	a1_str -> {},"
					+ "\n 	a2_str -> {(3,3),(4,4)},"
					+ "\n 	a3_str -> {(1,1)},"
					+ "\n"
					+ "\n 	c4_str -> {(f,f)},"
					+ "\n 	c3_str -> {},"
					+ "\n 	c2_str -> {(i,i),(j,j)},"
					+ "\n 	c1_str -> {(k,k)};"
					+ "\n 	"
					+ "\n arrows"
					+ "\n 	g3 -> {(1,a)},"
					+ "\n 	g2 -> {(3,a),(4,b)},"
					+ "\n 	g1 -> {},"
					+ "\n"
					+ "\n 	h3 -> {(1,f)},"
					+ "\n 	h2 -> {(3,i),(4,j)},"
					+ "\n 	h1 -> {}; "
					+ "\n} : C"
					+ "\n"
					+ "\ntransform t = {"
					+ "\n nodes"
					+ "\n 	b2 -> {(a,a),(b,b)},"
					+ "\n 	b1 -> {(d,d)},"
					+ "\n"
					+ "\n 	a3 -> {(1,1)},"
					+ "\n 	a2 -> {(3,3),(4,4)},"
					+ "\n	a1 -> {},"
					+ "\n "
					+ "\n 	c4 -> {(f,f)},"
					+ "\n 	c3 -> {},"
					+ "\n 	c2 -> {(i,i),(j,j)},"
					+ "\n 	c1 -> {(k,k)};"
					+ "\n} : I0 -> I"
					+ "\n"
					+ "\ninstance J0 = sigma F I0"
					+ "\n"
					+ "\ntransform t0 = sigma J0 J t";




}
