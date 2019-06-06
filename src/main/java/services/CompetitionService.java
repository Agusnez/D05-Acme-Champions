
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CompetitionRepository;
import security.Authority;
import domain.Actor;
import domain.Competition;
import domain.Game;
import domain.Minutes;
import domain.Referee;
import domain.Team;
import forms.CompetitionForm;

@Service
@Transactional
public class CompetitionService {

	//Managed Repository ---------------------------------------------------
	@Autowired
	private CompetitionRepository	competitionRepository;

	//Supporting services --------------------------------------------------

	@Autowired
	private ActorService			actorService;

	@Autowired
	private RefereeService			refereeService;

	@Autowired
	private GameService				gameService;

	@Autowired
	private MinutesService			minutesService;

	@Autowired
	private FormatService			formatService;

	@Autowired
	private FederationService		federationService;

	@Autowired
	private Validator				validator;


	//Simple CRUD methods --------------------------------------------------

	public CompetitionForm create() {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.FEDERATION);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		final CompetitionForm res = new CompetitionForm();

		return res;

	}

	public Competition reconstruct(final CompetitionForm form, final BindingResult binding) {

		final Competition res = new Competition();

		if (form.getId() == 0) {

			res.setFormat(this.formatService.findOne(form.getFormatId()));
			res.setFederation(this.federationService.findByPrincipal());
			res.setNameTrophy(form.getNameTrophy());
			res.setStartDate(form.getStartDate());
			res.setClosed(false);

		} else {

			final Competition oldOne = this.competitionRepository.findOne(form.getId());

			res.setClosed(oldOne.getClosed());
			res.setFederation(oldOne.getFederation());
			res.setFormat(oldOne.getFormat());
			res.setGames(oldOne.getGames());
			res.setId(oldOne.getId());
			res.setNameTrophy(oldOne.getNameTrophy());
			res.setStartDate(oldOne.getStartDate());
			res.setTeams(oldOne.getTeams());
			res.setVersion(oldOne.getVersion());

		}

		this.validator.validate(res, binding);

		return res;

	}

	public void addTeam(final Competition competition, final Team team) {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.FEDERATION);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		Collection<Team> teams = new ArrayList<>();

		if (competition.getTeams() != null)
			teams = competition.getTeams();

		teams.add(team);

		competition.setTeams(teams);

		this.competitionRepository.save(competition);

	}

	public Collection<Competition> findAll() {

		final Collection<Competition> competition = this.competitionRepository.findAll();

		Assert.notNull(competition);

		return competition;
	}

	public Competition findOne(final int ActorId) {

		final Competition competition = this.competitionRepository.findOne(ActorId);

		Assert.notNull(competition);

		return competition;
	}

	public Competition save(final Competition competition) {

		final Competition res;

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.FEDERATION);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		final Date currentDate = new Date(System.currentTimeMillis() - 1000);
		Assert.isTrue(competition.getStartDate().after(currentDate));

		if (competition.getId() == 0)
			res = this.competitionRepository.save(competition);
		else {

			if (competition.getClosed()) {

				final Date start = competition.getStartDate();

				final List<Referee> referees = (List<Referee>) this.refereeService.findAll();

				final Collection<Game> games = competition.getGames();

				final Collection<Team> teams = competition.getTeams();

				if (competition.getFormat().getType().equals("LEAGUE")) {

					for (final Team team : teams)
						for (final Team team2 : teams)
							if (!team.equals(team2)) {

								final Game game = new Game();

								start.setTime(start.getTime() + 86400000);

								final Random rand = new Random();
								final Referee random = referees.get(rand.nextInt(referees.size()));

								game.setFriendly(false);
								game.setGameDate(start);
								game.setHomeTeam(team);
								game.setPlace(team.getStadiumName());
								game.setVisitorTeam(team2);
								game.setReferee(random);

								final Game saved = this.gameService.save(game);

								games.add(saved);

							}

					competition.setGames(games);

					competition.setEndDate(start);

				} else {

					final List<Team> teamsThisRound = new ArrayList<>();

					teamsThisRound.addAll(competition.getTeams());

					final Integer gamesToPlay = competition.getTeams().size() / 2;

					for (int i = 0; i < gamesToPlay; i++) {

						final Random rand1 = new Random();
						final Team team1 = teamsThisRound.get(rand1.nextInt(teamsThisRound.size()));

						Random rand2 = new Random();
						Team team2 = teamsThisRound.get(rand2.nextInt(teamsThisRound.size()));

						while (team1.equals(team2)) {

							rand2 = new Random();
							team2 = teamsThisRound.get(rand2.nextInt(teamsThisRound.size()));

						}

						final Game game = new Game();

						start.setTime(start.getTime() + 86400000);

						final Random rand = new Random();
						final Referee random = referees.get(rand.nextInt(referees.size()));

						game.setFriendly(false);
						game.setGameDate(start);
						game.setHomeTeam(team1);
						game.setPlace(team1.getStadiumName());
						game.setVisitorTeam(team2);
						game.setReferee(random);

						final Game saved = this.gameService.save(game);

						teamsThisRound.remove(team1);
						teamsThisRound.remove(team2);

						games.add(saved);

					}

					competition.setGames(games);

					competition.setEndDate(start);

				}

			}

			res = this.competitionRepository.save(competition);

		}

		Assert.notNull(res);

		return res;
	}

	public void nextRounds(final Minutes minutes) {

		final Competition competition = this.competitionRepository.findCompetitionByGameId(minutes.getGame().getId());

		if (competition.getFormat().getType().equals("LEAGUE")) {

			//			if (gamesAll.size() == competition.getTeams().size()*(competition.getTeams().size()-1)*2) {
			//				
			//				for (Game game : games) {
			//					
			//					Minutes minutes = minutesService.findMinuteByGameId(game.getId());
			//					
			//					Map<Team, Integer> map = new HashMap<>();
			//					
			//				}
			//
			//				winner.setTrackRecord(winner.getTrackRecord() + 1);
			//				
			//				teamService.save(winner);
			//				
			//			}

		} else {

			final Collection<Game> gamesNow = competition.getGames();
			final Collection<Minutes> minutesNow = new ArrayList<>();

			for (final Game game : gamesNow) {

				final Minutes minutesFind = this.minutesService.findMinuteByGameId(game.getId());

				if (minutesFind != null)
					minutesNow.add(minutesFind);

			}

			final List<Referee> referees = (List<Referee>) this.refereeService.findAll();

			if (gamesNow.size() == minutesNow.size()) {

				List<Team> teams = new ArrayList<>();

				final Map<Team, Integer> nextTeams = new HashMap<>();

				for (final Game game : gamesNow) {

					final Minutes minute = this.minutesService.findMinuteByGameId(game.getId());

					final Integer value = nextTeams.get(minute.getWinner());
					nextTeams.put(minute.getWinner(), value == null ? 1 : value + 1);

				}

				Entry<Team, Integer> max = null;

				for (final Entry<Team, Integer> e : nextTeams.entrySet())
					if (max == null || e.getValue() == max.getValue()) {
						teams.add(e.getKey());
						if (max == null || e.getValue() > max.getValue()) {
							teams = new ArrayList<>();
							teams.add(e.getKey());
							max = e;
						}
					}

				final Integer gamesToPlay = teams.size() / 2;

				final Date start = new Date();

				start.setTime(minutes.getGame().getGameDate().getTime());

				for (int i = 0; i < gamesToPlay; i++) {

					final Random rand1 = new Random();
					final Team team1 = teams.get(rand1.nextInt(teams.size()));

					Random rand2 = new Random();
					Team team2 = teams.get(rand2.nextInt(teams.size()));

					while (team1.equals(team2)) {

						rand2 = new Random();
						team2 = teams.get(rand2.nextInt(teams.size()));

					}

					final Game game = new Game();

					start.setTime(start.getTime() + 86400000);

					final Random rand = new Random();
					final Referee random = referees.get(rand.nextInt(referees.size()));

					game.setFriendly(false);
					game.setGameDate(start);
					game.setHomeTeam(team1);
					game.setPlace(team1.getStadiumName());
					game.setVisitorTeam(team2);
					game.setReferee(random);

					final Game saved = this.gameService.saveAlgorithm(game);

					gamesNow.add(saved);

					teams.remove(team1);
					teams.remove(team2);

				}
				competition.setGames(gamesNow);

				this.competitionRepository.save(competition);

			}
		}

	}

	//Other business methods----------------------------

	public Boolean exist(final int competitiond) {
		Boolean res = false;

		final Competition competition = this.competitionRepository.findOne(competitiond);

		if (competition != null)
			res = true;

		return res;

	}

	public Boolean security(final int competitiond) {
		Boolean res = false;

		final Competition competition = this.competitionRepository.findOne(competitiond);

		final Actor loged = this.actorService.findByPrincipal();

		if (competition.getFederation().getId() == loged.getId())
			res = true;

		return res;

	}

	public Competition findCompetitionByGameId(final int gameId) {

		final Competition res = this.competitionRepository.findCompetitionByGameId(gameId);

		return res;
	}

	public Collection<Competition> findByFederationId(final int id) {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.FEDERATION);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		final Collection<Competition> res = this.competitionRepository.findByFederationId(id);

		return res;
	}

	public Collection<Competition> findByFormatId(final int id) {
		return this.competitionRepository.findByFormatId(id);
	}

}
