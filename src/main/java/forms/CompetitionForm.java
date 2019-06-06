
package forms;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class CompetitionForm {

	private String	nameTrophy;
	private Date	startDate;
	private int		formatId;
	private int		id;
	private int		version;


	public String getNameTrophy() {
		return this.nameTrophy;
	}

	public void setNameTrophy(final String nameTrophy) {
		this.nameTrophy = nameTrophy;
	}

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	public int getFormatId() {
		return this.formatId;
	}

	public void setFormatId(final int formatId) {
		this.formatId = formatId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

}
