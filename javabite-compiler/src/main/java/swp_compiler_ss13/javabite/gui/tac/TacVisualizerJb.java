package swp_compiler_ss13.javabite.gui.tac;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.visualization.TACVisualization;

public class TacVisualizerJb extends JFrame implements TACVisualization {
	
	public TacVisualizerJb() {
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
	}

	@Override
	public void visualizeTAC(List<Quadruple> tac) {
		table.setModel(new DefaultTableModel(
				new Object[tac.size()][5],
				new String[] {
					"Count", "Operation", "Arg1", "Arg2", "Result"
				}
			) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				Class<?>[] columnTypes = new Class[] {
					Integer.class, String.class, String.class, String.class, String.class
				};
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});
		TableModel model = table.getModel();
		int i = 0;
		for (Quadruple q:tac) {
			model.setValueAt(i, i, 0);
			model.setValueAt(q.getOperator().toString(), i, 1);
			model.setValueAt(q.getArgument1(), i, 2);
			model.setValueAt(q.getArgument2(), i, 3);
			model.setValueAt(q.getResult(), i, 4);
			i++;
		}
		this.setSize(600, 400);
		this.setVisible(true);
	}

	private static final long serialVersionUID = 1L;
	private JTable table;
}
