package catdata.aql;

public class AqlKBExample extends AqlExample {

	@Override
	public String getName() {
		return "KB";
	}

	@Override
	public String getText() {
		return s;
	}
	
	String s = "//one = two should not be provable "
			+ "\ntypeside EmptySortsCheck = literal { "
			+ "\n	types"
			+ "\n 		void "
			+ "\n 		nat"
			+ "\n	constants"
			+ "\n 		one two : nat"
			+ "\n 	equations  "
			+ "\n	 	 forall x:void. one = two "
			+ "\n	options"
			+ "\n		prover = completion"
			+ "\n//		completion_precedence = \"one two\""
			+ "\n} "
			+ "\n"
			+ "\ntypeside Group = literal {"
			+ "\n	types "
			+ "\n		S"
			+ "\n	constants"
			+ "\n		e : S"
			+ "\n	functions"
			+ "\n		I : S -> S"
			+ "\n		o : S,S -> S"
			+ "\n	equations"
			+ "\n 		forall x. (e o x) = x"
			+ "\n 		forall x. (I(x) o x) = e"
			+ "\n 		forall x, y, z. ((x o y) o z) = (x o (y o z))"
			+ "\n 	options"
			+ "\n		prover = completion"
			+ "\n//		completion_precedence = \"e o I\""
			+ "\n}"
			+ "\n"
			+ "\n"
			+ "\ntypeside LR = literal {"
			+ "\n	types "
			+ "\n		S"
			+ "\n	constants"
			+ "\n		e : S"
			+ "\n	functions"
			+ "\n		I : S -> S"
			+ "\n		o : S,S -> S"
			+ "\n	equations"
			+ "\n 		forall x. o(e,x) = x"
			+ "\n		forall x. o(x,I(x)) = e"
			+ "\n		forall x y z. o(o(x,y),z)=o(x,o(y,z))"
			+ "\n 	options"
			+ "\n		prover = completion"
			+ "\n//		completion_precedence = \"e o I\""
			+ "\n}"
			+ "\n"
			+ "\ntypeside RL = literal {"
			+ "\n	types "
			+ "\n		S"
			+ "\n	constants"
			+ "\n		e : S"
			+ "\n	functions"
			+ "\n		I : S -> S"
			+ "\n		o : S,S -> S"
			+ "\n	equations"
			+ "\n 		forall x. o(x,e) = x"
			+ "\n		forall x. o(I(x),x) = e"
			+ "\n		forall x, y, z. o(o(x,y),z)=o(x,o(y,z))"
			+ "\n 	options"
			+ "\n		prover = completion"
			+ "\n//		completion_precedence = \"e o I\""
			+ "\n}"
			+ "\n"
			+ "\ntypeside Arith = literal {"
			+ "\n	types"
			+ "\n		N"
			+ "\n	constants"
			+ "\n		zero : N"
			+ "\n	functions"
			+ "\n		succ : N -> N"
			+ "\n		plus : N,N -> N"
			+ "\n		times : N,N -> N  "
			+ "\n	equations"
			+ "\n		forall x. plus(zero, x) = x"
			+ "\n		forall x, y. plus(succ(x),y) = succ(plus(x,y))"
			+ "\n		forall x. times(zero, x) = zero"
			+ "\n		forall x, y. times(succ(x),y) = plus(y,times(x,y))"
			+ "\n	options"
			+ "\n		prover = completion"
			+ "\n//		completion_precedence = \"zero succ plus times\"	"
			+ "\n} "
			+ "\n "
			+ "\n//try ((a o b) o (c o d)) = ((a o c) o (b o d)) and"
			+ "\n//forall p q r s. ((p o q) o (r o s)) = ((p o r) o (q o s))"
			+ "\ntypeside Entropic = literal {"
			+ "\n	 types "
			+ "\n		S"
			+ "\n	constants"
			+ "\n		a b c d : S"
			+ "\n	 functions"
			+ "\n		o : S,S -> S"
			+ "\n	 equations"
			+ "\n	 	forall x, y, z, w. o(o(x,y),o(z,w)) = o(o(x,z),o(y,w))"
			+ "\n		forall x, y. o(o(x,y),x) = x"
			+ "\n	options"
			+ "\n		prover = completion"
			+ "\n		completion_precedence = \"o a b c d\"	"
			+ "\n} "
			+ "\n"
			+ "\ntypeside ACUIN = literal {"
			+ "\n	types "
			+ "\n		S"
			+ "\n	constants"
			+ "\n		e : S"
			+ "\n	functions"
			+ "\n		n : S -> S"
			+ "\n		o : S,S -> S"
			+ "\n	equations"
			+ "\n		forall x, y, z. o(o(x,y),z) = o(x,o(y,z))"
			+ "\n		forall x, y. o(x,y) = o(y,x)"
			+ "\n		forall x. o(x, e) = x"
			+ "\n//		forall x. o(x, x) = x"
			+ "\n//		forall x. o(x, x.n) = e"
			+ "\n	options"
			+ "\n		prover = completion"
			+ "\n		completion_precedence = \"e n o\"	"
			+ "\n}"
			+ "\n"
			+ "\n// still too hard"
			+ "\n/*"
			+ "\ntypeside BooleanRing = literal {"
			+ "\n	types"
			+ "\n		S"
			+ "\n	constants"
			+ "\n		zero one : S"
			+ "\n	functions"
			+ "\n		plus times : S,S -> S"
			+ "\n	equations"
			+ "\n		forall x y z.  plus(plus(x,y),z) = plus(x,plus(y,z))"
			+ "\n		forall x y z.  times(times(x,y),z) = times(x,times(y,z))"
			+ "\n		forall x y. plus(x,y) = plus(y,x)"
			+ "\n		forall x y. times(x,y) = times(y,x)"
			+ "\n		forall x y, z. plus(x,plus(y,z)) = plus(y,plus(x,z))"
			+ "\n		forall x y, z. times(x,times(y,z)) = times(y,times(x,z))"
			+ "\n		forall x y, z. times(x,plus(y,z)) = plus(times(x,y),times(x,z))"
			+ "\n		forall x y, z. times(plus(x,y),z) = plus(times(x,z),times(y,z))"
			+ "\n		forall x. plus(zero,x) = x"
			+ "\n		forall x. plus(x,zero) = x"
			+ "\n		forall x. times(one,x) = x"
			+ "\n		forall x. times(x,one) = x"
			+ "\n		forall x. times(zero,x) = zero"
			+ "\n		forall x. times(x,zero) = zero"
			+ "\n		forall x. plus(x,x) = zero"
			+ "\n		forall x. times(x,x) = x"
			+ "\n		forall x y. plus(x,plus(x,y)) = y"
			+ "\n		forall x y. times(x,times(x,y)) = times(x,y)"
			+ "\n	options"
			+ "\n		prover = completion"
			+ "\n		completion_precedence = \"zero one plus times\""
			+ "\n}"
			+ "\n*/"
			+ "\n";





}
