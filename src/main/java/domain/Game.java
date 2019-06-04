
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = @Index(columnList = "home_team, visitor_team"))
public class Game extends DomainEntity {

	private Date	gameDate;
	private String	place;
	private Boolean	friendly;
	private Team	homeTeam;
	private Team	visitorTeam;
	private Referee	referee;


	@NotNull
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
	public Date getGameDate() {
		return this.gameDate;
	}

	public void setGameDate(final Date gameDate) {
		this.gameDate = gameDate;
	}

	@SafeHtml
	@NotBlank
	public String getPlace() {
		return this.place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Team getHomeTeam() {
		return this.homeTeam;
	}

	public void setHomeTeam(final Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	@Valid
	@NotNull
	@ManyToOne(optional = false)
	public Team getVisitorTeam() {
		return this.visitorTeam;
	}

	public void setVisitorTeam(final Team visitorTeam) {
		this.visitorTeam = visitorTeam;
	}

	@NotNull
	public Boolean getFriendly() {
		return this.friendly;
	}

	public void setFriendly(final Boolean friendly) {
		this.friendly = friendly;
	}

	@Valid
	@ManyToOne(optional = false)
	public Referee getReferee() {
		return this.referee;
	}

	public void setReferee(final Referee referee) {
		this.referee = referee;
	}

}
