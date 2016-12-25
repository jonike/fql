package catdata.fql.examples;

import catdata.ide.Example;
import catdata.ide.Language;

public class TypedFoilExample extends Example {

	@Override
	public Language lang() {
		return Language.FQL;
	}
	
	@Override
	public String getName() {
		return "FOIL";
	}

	@Override
	public String getText() {
		return s;
	}
	
	private final String s =
			"schema Begin = {"
					+ "\n nodes a, b, c, d;"
					+ "\n attributes"
					+ "\n	atta:a->string,"
					+ "\n	attb:b->string,"
					+ "\n	attc:c->string,"
					+ "\n	attd:d->string;"
					+ "\n arrows;"
					+ "\n equations;"
					+ "\n}"
					+ "\n"
					+ "\nschema Added = {"
					+ "\n nodes aPLUSb, cPLUSd;"
					+ "\n attributes"
					+ "\n	attab:aPLUSb->string,"
					+ "\n	attcd:cPLUSd->string;"
					+ "\n arrows;"
					+ "\n equations;"
					+ "\n}"
					+ "\n"
					+ "\nschema Multiplied = {"
					+ "\n nodes aPLUSbTIMEScPLUSd;"
					+ "\n attributes"
					+ "\n	attab : aPLUSbTIMEScPLUSd -> string,"
					+ "\n	attcd : aPLUSbTIMEScPLUSd -> string;"
					+ "\n arrows;"
					+ "\n equations;"
					+ "\n}"
					+ "\n"
					+ "\nmapping F = {"
					+ "\n nodes"
					+ "\n	a->aPLUSb,"
					+ "\n	b->aPLUSb,"
					+ "\n	c->cPLUSd,"
					+ "\n	d->cPLUSd;"
					+ "\n attributes"
					+ "\n	atta->attab,"
					+ "\n	attb->attab,"
					+ "\n	attc->attcd,"
					+ "\n	attd->attcd;"
					+ "\n arrows;"
					+ "\n}:Begin->Added"
					+ "\n"
					+ "\nmapping G = {"
					+ "\n nodes "
					+ "\n	aPLUSb->aPLUSbTIMEScPLUSd,"
					+ "\n	cPLUSd->aPLUSbTIMEScPLUSd;"
					+ "\n attributes"
					+ "\n	attab->attab,"
					+ "\n	attcd->attcd;"
					+ "\n arrows;"
					+ "\n}:Added->Multiplied"
					+ "\n"
					+ "\n/* Below, put any number of elements into a,b,c,d. "
					+ "\n * The output should have (a+b)*(c+d) many elements "
					+ "\n */"
					+ "\n"
					+ "\ninstance I = {"
					+ "\n nodes"
					+ "\n	a -> {1},"
					+ "\n	b -> {1,2},"
					+ "\n	c -> {1,2},"
					+ "\n	d -> {1,2,3};"
					+ "\n attributes"
					+ "\n	atta -> {(1,a1)},"
					+ "\n	attb -> {(1,b1),(2,b2)},"
					+ "\n	attc -> {(1,c1),(2,c2)},"
					+ "\n	attd -> {(1,d1),(2,d2),(3,d3)};"
					+ "\n arrows;	"
					+ "\n}: Begin"
					+ "\n"
					+ "\n/*(1+2)*(2+3)=15*/"
					+ "\n"
					+ "\ninstance J = sigma F I"
					+ "\ninstance K =pi G J";
}