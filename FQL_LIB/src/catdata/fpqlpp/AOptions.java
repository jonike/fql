package catdata.fpqlpp;

import java.awt.GridLayout;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import catdata.Pair;
import catdata.Unit;
import catdata.ide.Language;
import catdata.ide.Options;

public class AOptions extends Options {

	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return Language.FPQLPP.toString();
	}
	

	//@Override
	public Pair<JComponent, Function<Unit, Unit>> display() {
		JPanel general1 = new JPanel(new GridLayout(3, 1));
		JPanel general2 = new JPanel(new GridLayout(3, 1));

		JSplitPane generalsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		generalsplit.add(general1);
		generalsplit.add(general2);

			
		Function<Unit, Unit> fn = new Function<Unit, Unit>() {

			@Override
			public Unit apply(Unit t) {
				try {
	
				} catch (NumberFormatException nfe) {
				}
				
			
				return new Unit();
			}
			
		};

		return new Pair<>(generalsplit, fn);
	}


	@Override
	public int size() {
		return 0;
	} 

}
