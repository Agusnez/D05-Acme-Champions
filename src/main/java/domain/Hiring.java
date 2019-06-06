
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = @Index(columnList = "status"))
public class Hiring extends DomainEntity {

	private Double		price;
	private String		status;
	private String		mandatoryComment;

	private Manager		manager;
	private President	president;


	@NotNull
	@DecimalMin(value = "0.0")
	public Double getPrice() {
		return this.price;
	}

	public void setPrice(final Double price) {
		this.price = price;
	}

	@NotBlank
	@Pattern(regexp = "\\AREJECTED\\z|\\AACCEPTED\\z|\\APENDING\\z")
	@SafeHtml
	public String getStatus() {
		return this.status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	@SafeHtml
	public String getMandatoryComment() {
		return this.mandatoryComment;
	}

	public void setMandatoryComment(final String mandatoryComment) {
		this.mandatoryComment = mandatoryComment;
	}

	@Valid
	@ManyToOne(optional = false)
	public Manager getManager() {
		return this.manager;
	}

	public void setManager(final Manager manager) {
		this.manager = manager;
	}

	@Valid
	@ManyToOne(optional = false)
	public President getPresident() {
		return this.president;
	}

	public void setPresident(final President president) {
		this.president = president;
	}

}
