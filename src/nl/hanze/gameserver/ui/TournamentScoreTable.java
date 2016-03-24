package nl.hanze.gameserver.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import nl.hanze.gameserver.server.Tournament;

public class TournamentScoreTable extends JTable {

	private static final long serialVersionUID = 0L;
	
	private TournamentScoreTableModel tableModel;
	private TableCellRenderer tableCellRenderer;
	
	public TournamentScoreTable() {
		super();
		tableModel = new TournamentScoreTableModel();
		createTableCellRenderer();
		setModel(tableModel);
	}
	
	public void setTournament(Tournament tournament) {
		tableModel.setTournament(tournament);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if(column == 0) {
			return super.getCellRenderer(row, column);
		} else {
			return tableCellRenderer;
		}
	}
	
	private void createTableCellRenderer() {
		tableCellRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 0L;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				int score = (Integer) value;
				String text = "";
				Color color = Color.WHITE;
				
				if(score == TournamentScoreTableModel.SCORE_UNABLE) {
					text = "";
					color = Color.BLACK;
				} else if(score == TournamentScoreTableModel.SCORE_UNAVAILABLE) {
					text = "";
					color = Color.WHITE;
				} else if(score == TournamentScoreTableModel.SCORE_BUSY) {
					text = "Busy";
					color = Color.GRAY;
				} else if(score == TournamentScoreTableModel.SCORE_WIN) {
					text = "WIN";
					color = Color.GREEN;
				} else if(score == TournamentScoreTableModel.SCORE_LOSS) {
					text = "LOSS";
					color = Color.RED;
				} else if(score == TournamentScoreTableModel.SCORE_DRAW) {
					text = "DRAW";
					color = Color.BLUE;
				}
				
				Component cell = super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
				cell.setBackground(color);
				
				return cell;
			}
		};
	}

}
