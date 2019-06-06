
package services;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.MinutesRepository;
import security.Authority;
import domain.Actor;
import domain.Competition;
import domain.Game;
import domain.Minutes;
import domain.Player;
import domain.StatisticalData;

@Service
@Transactional
public class MinutesService {

	// Managed repository

	@Autowired
	private MinutesRepository		minutesRepository;

	// Supporting Services
	@Autowired
	private ActorService			actorService;

	@Autowired
	private StatisticalDataService	statisticalDataService;

	@Autowired
	private PlayerService			playerService;

	@Autowired
	private CompetitionService		competitionService;

	@Autowired
	private GameService				gameService;


	//simple CRUD methods

	public Minutes create(final Game game) {

		final Minutes result = new Minutes();

		final Collection<Player> playersScore = new HashSet<Player>();
		final Collection<Player> playersYellow = new HashSet<Player>();
		final Collection<Player> playersRed = new HashSet<Player>();

		result.setPlayersRed(playersRed);
		result.setPlayersYellow(playersYellow);
		result.setPlayersScore(playersScore);
		result.setClosed(false);
		result.setGame(game);
		result.setHomeScore(0);
		result.setVisitorScore(0);
		result.setWinner(null);

		return result;

	}

	public Collection<Minutes> findAll() {

		final Collection<Minutes> result = this.minutesRepository.findAll();
		Assert.notNull(result);
		return result;

	}

	public Minutes findOne(final int minutesId) {

		final Minutes result = this.minutesRepository.findOne(minutesId);
		return result;

	}

	public Minutes save(final Minutes minutes) {
		Assert.notNull(minutes);

		final Authority authReferee = new Authority();
		authReferee.setAuthority(Authority.REFEREE);

		//Hay que estar logeado
		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);

		//solo puede guardar partidos referee's
		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authReferee));

		//la persona que guarda el minutes es la misma que creó o gestionó el partido
		final Collection<Game> gamesByReferee = this.gameService.findGameByRefereeId(actor.getId());
		Assert.isTrue(gamesByReferee.contains(minutes.getGame()));

		//un partido solo puede tener 1 minutes
		if (minutes.getId() == 0)
			Assert.isTrue(this.CountMinutesByGameId(minutes.getGame().getId()) == 0);
		else
			Assert.isTrue(this.CountMinutesByGameId(minutes.getGame().getId()) == 1);

		//el partido debe haber acabado
		final Date currentDate = new Date(System.currentTimeMillis() - 1000);
		Assert.isTrue(minutes.getGame().getGameDate().before(currentDate));

		//cuando el minutes está cerrado aplicamos su efecto en los actores
		if (minutes.getClosed()) {
			//tratamiento de goles, calculo del score y ganador
			final Collection<Player> playersScore = minutes.getPlayersScore();
			int goalsHome = 0;
			int goalsVisitor = 0;
			for (final Player p : playersScore) {
				final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(p.getId());
				statisticalData.setGoals(statisticalData.getGoals() + 1);
				if (p.getTeam().getStadiumName().equals(minutes.getGame().getPlace()))
					goalsHome = goalsHome + 1;
				else
					goalsVisitor = goalsVisitor + 1;
			}
			minutes.setHomeScore(goalsHome);
			minutes.setVisitorScore(goalsVisitor);
			if (goalsHome > goalsVisitor)
				minutes.setWinner(minutes.getGame().getHomeTeam());
			else if (goalsHome < goalsVisitor)
				minutes.setWinner(minutes.getGame().getVisitorTeam());
			else
				minutes.setWinner(null);

			//actualización partidos jugados
			final Game gameOfMinutes = minutes.getGame();
			final Collection<Player> playersHome = this.playerService.findPlayersOfTeam(gameOfMinutes.getHomeTeam().getId());
			final Collection<Player> playersVisitor = this.playerService.findPlayersOfTeam(gameOfMinutes.getVisitorTeam().getId());
			final Collection<Player> playersOfTheGame = new HashSet<Player>();
			playersOfTheGame.addAll(playersVisitor);
			playersOfTheGame.addAll(playersHome);

			for (final Player p : playersOfTheGame) {
				final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(p.getId());
				statisticalData.setMatchsPlayed(statisticalData.getMatchsPlayed() + 1);
				//antes de ver amarillas y rojas reseteamos los que estuvieran castigados
				//de partido anterior
				p.setPunished(false);
				this.playerService.save(p);
			}

			//tratamiento de amarillas
			for (final Player p : minutes.getPlayersYellow()) {
				final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(p.getId());
				final int yellowCardsAccumulated = statisticalData.getAccumulatedYellowCard() + 1;
				if (yellowCardsAccumulated == 5) {
					p.setPunished(true);
					statisticalData.setAccumulatedYellowCard(0);
					this.playerService.save(p);
				} else
					statisticalData.setAccumulatedYellowCard(yellowCardsAccumulated);
				statisticalData.setYellowCards(yellowCardsAccumulated);
				this.statisticalDataService.save(statisticalData);
			}

			//tratamiento de rojas
			for (final Player p : minutes.getPlayersRed()) {
				final StatisticalData statisticalData = this.statisticalDataService.findStatisticalDataByPlayerId(p.getId());
				final int redCardsAccumulated = statisticalData.getRedCards();
				statisticalData.setRedCards(redCardsAccumulated + 1);
				this.statisticalDataService.save(statisticalData);
				p.setPunished(true);
				this.playerService.save(p);
			}
		}

		final Minutes result = this.minutesRepository.save(minutes);

		return result;

	}
	//Other business methods

	public Minutes findMinuteByGameId(final int gameId) {
		final Minutes res = this.minutesRepository.findMinuteByGameId(gameId);
		return res;
	}

	public boolean security(final int playerId, final int minutesId) {

		final Player player = this.playerService.findOne(playerId);
		final Minutes minutes = this.findOne(minutesId);

		final Actor actor = this.actorService.findByPrincipal();
		boolean res = true;

		if (player == null || minutes == null)
			res = false;
		else {
			final Collection<Player> playersHome = this.playerService.findPlayersOfTeam(minutes.getGame().getHomeTeam().getId());
			final Collection<Player> playersVisitor = this.playerService.findPlayersOfTeam(minutes.getGame().getVisitorTeam().getId());
			if (minutes.getClosed() || actor == null || (!playersHome.contains(player) && !playersVisitor.contains(player)) || minutes.getGame().getReferee().getId() != actor.getId())
				res = false;
		}
		return res;
	}
	public void addPlayerScored(final int playerId, final int minutesId) {

		final Minutes minutes = this.findOne(minutesId);
		final Player player = this.playerService.findOne(playerId);

		final Collection<Player> playersScored = minutes.getPlayersScore();
		playersScored.add(player);

		minutes.setPlayersScore(playersScored);

		final Integer countHome = this.playerService.countHomeGoalsByMinutesId(minutesId);
		final Integer countVisitor = this.playerService.countVisitorGoalsByMinutesId(minutesId);

		minutes.setHomeScore(countHome);
		minutes.setVisitorScore(countVisitor);

		this.save(minutes);

	}

	public void addPlayerYellowCard(final int playerId, final int minutesId) {
		final Minutes minutes = this.findOne(minutesId);
		final Player player = this.playerService.findOne(playerId);

		//controlamos que no se pongan dos amarillas, aconsejamos al usuario que añada una roja
		//en ese caso
		final Integer countYellow = this.playerService.countYellowsOfPlayerByMinutePlayerId(minutesId, playerId);

		try {
			Assert.isTrue(countYellow == 0);
		} catch (final Exception e) {
			throw new DataIntegrityViolationException("two-yellows");
		}

		final Collection<Player> playersYellowCards = minutes.getPlayersYellow();
		playersYellowCards.add(player);

		minutes.setPlayersYellow(playersYellowCards);

		final Collection<Player> playersRedCards = minutes.getPlayersRed();
		playersRedCards.remove(player);

		this.save(minutes);
	}
	public void addPlayerRedCard(final int playerId, final int minutesId) {
		final Minutes minutes = this.findOne(minutesId);
		final Player player = this.playerService.findOne(playerId);

		//controlamos que no se pongan dos rojas, aconsejamos al usuario que lo corrija
		final Integer countRed = this.playerService.countRedOfPlayerByMinutePlayerId(minutesId, playerId);
		try {
			Assert.isTrue(countRed == 0);
		} catch (final Exception e) {
			throw new DataIntegrityViolationException("two-reds");
		}

		final Collection<Player> playersRedCards = minutes.getPlayersRed();
		playersRedCards.add(player);

		final Collection<Player> playerYellowCards = minutes.getPlayersYellow();
		playerYellowCards.remove(player);

		minutes.setPlayersRed(playersRedCards);

		this.save(minutes);

	}

	public void clearMinutes(final int minutesId) {

		final Minutes result = this.findOne(minutesId);

		final Collection<Player> playersScore = new HashSet<Player>();
		final Collection<Player> playersYellow = new HashSet<Player>();
		final Collection<Player> playersRed = new HashSet<Player>();

		result.setPlayersRed(playersRed);
		result.setPlayersYellow(playersYellow);
		result.setPlayersScore(playersScore);
		result.setHomeScore(0);
		result.setVisitorScore(0);
	}

	public void closeMinutes(final int minutesId) {
		final Minutes result = this.findOne(minutesId);
		final Competition competition = this.competitionService.findCompetitionByGameId(result.getGame().getId());
		result.setClosed(true);
		if (result.getHomeScore() == result.getVisitorScore()) {
			if (!result.getGame().getFriendly())
				try {
					Assert.isTrue(competition.getFormat().getType().equals("LEAGUE"));
				} catch (final Exception e) {
					throw new DataIntegrityViolationException("tie-tournament");
				}
			result.setWinner(null);

		} else if (result.getHomeScore() > result.getVisitorScore())
			result.setWinner(result.getGame().getHomeTeam());
		else
			result.setWinner(result.getGame().getVisitorTeam());
		this.save(result);

	}
	public Integer CountMinutesByGameId(final int gameId) {
		Assert.notNull(gameId);
		final Integer res = this.minutesRepository.CountMinutesByGameId(gameId);
		return res;
	}

	public void flush(final int minutesId) {
		this.minutesRepository.flush();
	}

}
