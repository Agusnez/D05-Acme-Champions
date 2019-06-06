
package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Game;
import domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class GameServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private GameService		gameService;

	@Autowired
	private TeamService		teamService;

	@Autowired
	private RefereeService	refereeService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 29.2: An actor who is not authenticated must be able to: View next matches as well as the already finished ones.
	 * 
	 * b) Negative cases:
	 * 2. The number of games is incorrect
	 * 3. The number of games is null
	 * 
	 * c) Sentence coverage
	 * -findAllGamesOrdered():100%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */

	@Test
	public void driverListGamesOrdered() {
		final Object testingData[][] = {
			{
				7, null
			},//1. All fine
			{
				10, IllegalArgumentException.class
			},//2. The number of games is incorrect
			{
				null, NullPointerException.class
			},//3. The number of games is null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListGamesOrdered((Integer) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListGamesOrdered(final Integer number, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();

			final Collection<Game> games = this.gameService.findAllGamesOrdered();

			Assert.isTrue(games.size() == number);
		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 31.1: Actors that are authenticated as a referee must be able to: Manage their matches: Create match
	 * 
	 * b) Negative cases:
	 * 2. Player is trying to create a game
	 * 3. Game date is past
	 * 
	 * c) Sentence coverage
	 * -create()=100%
	 * -save()=74.4%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */
	@Test
	public void driverCreateGame() {
		final Object testingData[][] = {
			{
				"2019/09/30 10:00", "team1", "team2", "referee1", null
			},//1. All fine
			{
				"2019/09/30 10:00", "team1", "team2", "player1", IllegalArgumentException.class
			},//2. Player is trying to create a game
			{
				"2018/09/24 10:00", "team1", "team2", "referee1", IllegalArgumentException.class
			},//3. Game date is past

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateGame(this.convertStringToDate((String) testingData[i][0]), (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateCreateGame(final Date dateGame, final String teamLocal, final String teamVisitor, final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Team local = this.teamService.findOne(super.getEntityId(teamLocal));
			final Team visitor = this.teamService.findOne(super.getEntityId(teamVisitor));

			final Game game = this.gameService.create();

			game.setHomeTeam(local);
			game.setVisitorTeam(visitor);
			game.setReferee(this.refereeService.findByPrincipal());
			game.setGameDate(dateGame);
			game.setPlace(local.getStadiumName());

			this.gameService.save(game);
			this.gameService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 31.1: Actors that are authenticated as a referee must be able to: Manage their matches: Edit match
	 * 
	 * b) Negative cases:
	 * 2. Player is trying to edit a game
	 * 3. Game date is past
	 * 
	 * c) Sentence coverage
	 * -findOne()=100%
	 * -save()=88.5%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */

	@Test
	public void driverEditGame() {
		final Object testingData[][] = {
			{
				"2019/09/24 10:00", "referee3", "game4", null
			},//1. All fine
			{
				"2019/09/22 10:00", "player1", "game1", IllegalArgumentException.class
			},//2. Player is trying to edit a game
			{
				"2017/09/24 10:00", "referee3", "game4", IllegalArgumentException.class
			},//3. Game date is past

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditGame(this.convertStringToDate((String) testingData[i][0]), (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateEditGame(final Date start, final String username, final String gameToEdit, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Game game = this.gameService.findOne(super.getEntityId(gameToEdit));

			game.setGameDate(start);

			this.gameService.save(game);
			this.gameService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 31.1: Actors that are authenticated as a referee must be able to: Manage their matches: Delete match
	 * 
	 * b) Negative cases:
	 * 2. Player is trying to delete a game
	 * 3. Game date is past
	 * 
	 * c) Sentence coverage
	 * -findOne()=100%
	 * -delete()=89%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */

	@Test
	public void driverDeleteGame() {
		final Object testingData[][] = {
			{
				"referee3", "game4", null
			},//1. All fine
			{
				"player1", "game4", IllegalArgumentException.class
			},//2. Player is trying to delete a game
			{
				"referee1", "game4", IllegalArgumentException.class
			},//2. The referee is not referee of the game

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteGame((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteGame(final String username, final String gameToDelete, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Game game = this.gameService.findOne(super.getEntityId(gameToDelete));
			this.gameService.delete(game);
		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 31.1: Actors that are authenticated as a referee must be able to: Manage their matches: List matches
	 * 
	 * b) Negative cases:
	 * 2.The number of games is incorrect
	 * 3.The number of games is null
	 * 
	 * c) Sentence coverage
	 * -findGameByRefereeId()=100%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */

	@Test
	public void driverListGamesByReferee() {
		final Object testingData[][] = {
			{
				"referee3", 1, null
			},//1. All fine
			{
				"referee3", 7, IllegalArgumentException.class
			},//2. The number of games is incorrect
			{
				"referee3", null, NullPointerException.class
			},//3. The number of games is null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListGamesByReferee((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateListGamesByReferee(final String username, final Integer number, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Collection<Game> games = this.gameService.findGameByRefereeId(this.refereeService.findByPrincipal().getId());

			Assert.isTrue(games.size() == number);
		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 31.1: Actors that are authenticated as a referee must be able to: Manage their matches: Display match
	 * 
	 * b) Negative cases:
	 * 2. Not match
	 * 3. Null object
	 * 
	 * c) Sentence coverage
	 * -findOne():100%
	 * -
	 * d) Data coverage
	 * -Training: 0%
	 */

	@Test
	public void driverDisplayTraining() {
		final Object testingData[][] = {

			{
				"game4", null
			},//1. All fine
			{
				"player2", IllegalArgumentException.class
			},//2. Not training
			{
				null, AssertionError.class
			},//3. Null object

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplayTraining((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateDisplayTraining(final String gameId, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Game game = this.gameService.findOne(super.getEntityId(gameId));

			Assert.isTrue(game != null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	//Methods-----------------------------------------------------------------------------------------------------------
	protected Date convertStringToDate(final String dateString) {
		Date date = null;

		if (dateString != null) {
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			try {
				date = df.parse(dateString);
			} catch (final Exception ex) {
				System.out.println(ex);
			}
		}

		return date;
	}

}
