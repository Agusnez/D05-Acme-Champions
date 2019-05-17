
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.PROPERTY)
public class Minutes extends DomainEntity {

	private String				score;
	private Collection<String>	playersScore;
	private Collection<String>	playersYellow;
	private Collection<String>	playersRed;


	public String getScore() {
		return this.score;
	}

	public void setScore(final String score) {
		this.score = score;
	}

	public Collection<String> getPlayersScore() {
		return this.playersScore;
	}

	public void setPlayersScore(final Collection<String> playersScore) {
		this.playersScore = playersScore;
	}

	public Collection<String> getPlayersYellow() {
		return this.playersYellow;
	}

	public void setPlayersYellow(final Collection<String> playersYellow) {
		this.playersYellow = playersYellow;
	}

	public Collection<String> getPlayersRed() {
		return this.playersRed;
	}

	public void setPlayersRed(final Collection<String> playersRed) {
		this.playersRed = playersRed;
	}

}
