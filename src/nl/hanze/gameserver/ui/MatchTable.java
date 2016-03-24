package nl.hanze.gameserver.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Match;

public class MatchTable extends JTable {

	private static final long serialVersionUID = 0L;
	
	private TableCellRenderer tableCellRenderer;
	private MatchTableModel tableModel;
	
	public MatchTable() {
		super();
		tableCellRenderer = createTableCellRenderer();
		tableModel = new MatchTableModel(Application.getInstance().getGameServer().getClientManager());
		setModel(tableModel);
		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addMouseListener(createMouseAdapter());
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return tableCellRenderer;
	}
	
	private MouseAdapter createMouseAdapter() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getSource() == MatchTable.this) {
					if(e.getClickCount() == 2) {
						Point p = e.getPoint();
						int row = MatchTable.this.rowAtPoint(p);
						int col = MatchTable.this.columnAtPoint(p);
						Match match = (Match) MatchTable.this.getValueAt(row,col);
						
						if(match != null && match.getGameModule().getView() != null) {
							new MatchView(match);
						}
					}
				}
			}
		};
	}
	
	private TableCellRenderer createTableCellRenderer() {
		return new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 0L;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Match match = (Match) value;
				
				String text = "";
				if(column == 0) {
					text = Integer.toString(match.getMatchNumber());
				} else if(column == 1) {
					text = match.getGameType();
				} else if(column == 2) {
					text = match.getPlayerOne().getPlayerName();
				} else if(column == 3) {
					text = match.getPlayerTwo().getPlayerName();
				}
				
				return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
			}
		};
	}

}
