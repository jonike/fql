package catdata.aql;

import java.util.Arrays;
import java.util.function.Function;

import javax.swing.JComponent;

import catdata.Pair;
import catdata.Unit;
import catdata.ide.CodeTextPanel;
import catdata.ide.Language;
import catdata.ide.Options;

public final class AqlOptionsDefunct extends Options {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return Language.AQL.toString();
	}

	@Override
	public Pair<JComponent, Function<Unit, Unit>> display() {
		return new Pair<>(new CodeTextPanel("", "Aql options are specified as pragmas in each Aql file.  Here are the available options: " + Arrays.toString(AqlOption.values())), x -> x);
	}

	@Override
	public int size() {
		return 1;
	}

}
