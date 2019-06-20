
package services;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Manager;
import domain.Player;
import domain.President;
import domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class PlayerServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private PlayerService		playerService;

	@Autowired
	private ManagerService		managerService;

	@Autowired
	private PresidentService	presidentService;

	@Autowired
	private TeamService			teamService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 25.1: An actor who is not authenticated must be able to register to the system as a player.
	 * 
	 * b) Negative cases:
	 * 2. SquadName = blank
	 * 3. SquadName = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 64,2%
	 * 
	 * d) Data coverage
	 * -Player: 7,14286%
	 */

	@Test
	public void driverRegisterPlayer() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "7", "Raúl", "playerTest", "playerTest", null
			},//1. All fine
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "7", "		", "playerTest", "playerTest", ConstraintViolationException.class
			},//2. SquadName = blank
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "7", null, "playerTest", "playerTest", ConstraintViolationException.class
			},//3. SquadName = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterPlayer((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], Integer.valueOf((String) testingData[i][8]), (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (Class<?>) testingData[i][12]);
	}

	protected void templateRegisterPlayer(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String positionEnglish, final String positionSpanish, final Integer squadNumber,
		final String squadName, final String username, final String password, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Player player = this.playerService.create();

			player.setName(name);
			player.setSurnames(surnames);
			player.setPhoto(photo);
			player.setEmail(email);
			player.setPhone(phone);
			player.setAddress(address);

			player.setBuyoutClause(0.0);
			player.setInjured(false);
			player.setPositionEnglish(positionEnglish);
			player.setPositionSpanish(positionSpanish);
			player.setSquadName(squadName);
			player.setSquadNumber(squadNumber);
			player.setPunished(false);

			player.getUserAccount().setUsername(username);
			player.getUserAccount().setPassword(password);

			this.playerService.save(player);
			this.playerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 26.2: An actor who is authenticated must be able to edit their personal data.
	 * 
	 * b) Negative cases:
	 * 2. SquadNumber < 1
	 * 3. SquadNumber > 99
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 30,2%
	 * 
	 * d) Data coverage
	 * -Player: 7,14286%
	 */

	@Test
	public void driverEditPlayer() {
		final Object testingData[][] = {
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "7", "Raúl", "player1", "player1", null
			},//1. All fine
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "-7", "Raúl", "player1", "player1", ConstraintViolationException.class
			},//2. SquadNumber < 1
			{
				"name1", "surnames", "https://google.com", "email1@gmail.com", "672195205", "address1", "GOALKEEPER", "PORTERO", "200", "Raúl", "player1", "player1", ConstraintViolationException.class
			},//3. SquadNumber > 99

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditPlayer((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], Integer.valueOf((String) testingData[i][8]), (String) testingData[i][9], (String) testingData[i][10], (String) testingData[i][11], (Class<?>) testingData[i][12]);
	}

	protected void templateEditPlayer(final String name, final String surnames, final String photo, final String email, final String phone, final String address, final String positionEnglish, final String positionSpanish, final Integer squadNumber,
		final String squadName, final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Player player = this.playerService.findOne(super.getEntityId(bean));

			player.setName(name);
			player.setSurnames(surnames);
			player.setPhoto(photo);
			player.setEmail(email);
			player.setPhone(phone);
			player.setAddress(address);

			player.setBuyoutClause(0.0);
			player.setInjured(false);
			player.setPositionEnglish(positionEnglish);
			player.setPositionSpanish(positionSpanish);
			player.setSquadName(squadName);
			player.setSquadNumber(squadNumber);
			player.setPunished(false);

			this.playerService.save(player);
			this.playerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 27.3: An actor who is authenticated as president must be able to: List all the players that are joined to the team.
	 * 
	 * b) Negative cases:
	 * 2. Invalid player
	 * 3. Unexpected number
	 * 
	 * c) Sentence coverage
	 * -findPlayersOfTeam(): 100%
	 * -findTeamByPresidentId()=100%
	 * 
	 * d) Data coverage
	 * -Player: 0%
	 */

	@Test
	public void driverListPlayerPresident() {
		final Object testingData[][] = {
			{
				"president1", "player1", 5, null
			},//1. All fine
			{
				"president1", "player8", 5, IllegalArgumentException.class
			},//2. Invalid player
			{
				"president1", "player1", 15, IllegalArgumentException.class
			},//3. Unexpected number

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListPlayerPresident((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateListPlayerPresident(final String usernamePresident, final String beanPlayer, final Integer expectedNumberOfPlayer, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(usernamePresident);

			final President president = this.presidentService.findOne(super.getEntityId(usernamePresident));

			final Player player = this.playerService.findOne(super.getEntityId(beanPlayer));

			Assert.isTrue(player != null);

			final Team team = this.teamService.findTeamByPresidentId(president.getId());

			final Collection<Player> players = this.playerService.findPlayersOfTeam(team.getId());

			final Integer sizePlayer = players.size();

			Assert.isTrue(sizePlayer == expectedNumberOfPlayer);

			Assert.isTrue(players.contains(player));

			this.playerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 27.4: An actor who is authenticated as president must be able to fire a player.
	 * 
	 * b) Negative cases:
	 * 2. Invalid authority
	 * 3. Not player
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 35,3%
	 * 
	 * d) Data coverage
	 * -Player: 0%
	 */

	@Test
	public void driverFirePlayer() {
		final Object testingData[][] = {
			{
				"president1", "player1", null
			},//1. All fine
			{
				"spononsor1", "player1", IllegalArgumentException.class
			},//2. Invalid authority
			{
				"president1", "manager1", IllegalArgumentException.class
			},//3. Not player

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateFirePlayer((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateFirePlayer(final String usernamePresident, final String beanPlayer, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(usernamePresident);

			final Player player = this.playerService.findOne(super.getEntityId(beanPlayer));

			Assert.isTrue(player != null);

			player.setTeam(null);

			this.playerService.save(player);
			this.playerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 28.4: An actor who is authenticated as manager must be able to list his/her players.
	 * 
	 * b) Negative cases:
	 * 2. Invalid player
	 * 3. Unexpected number
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -findPlayersOfTeam(): 100%
	 * 
	 * d) Data coverage
	 * -Player: 0%
	 */

	@Test
	public void driverListPlayer() {
		final Object testingData[][] = {
			{
				"manager1", "player1", 5, null
			},//1. All fine
			{
				"manager1", "player8", 5, IllegalArgumentException.class
			},//2. Invalid player
			{
				"manager1", "player1", 15, IllegalArgumentException.class
			},//3. Unexpected number

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListPlayer((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateListPlayer(final String usernameManager, final String beanPlayer, final Integer expectedNumberOfPlayer, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(usernameManager);

			final Manager manager = this.managerService.findOne(super.getEntityId(usernameManager));

			final Player player = this.playerService.findOne(super.getEntityId(beanPlayer));

			Assert.isTrue(player != null);

			final Collection<Player> players = this.playerService.findPlayersOfTeam(manager.getTeam().getId());

			final Integer sizePlayer = players.size();

			Assert.isTrue(sizePlayer == expectedNumberOfPlayer);

			Assert.isTrue(players.contains(player));

			this.playerService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}
	/*
	 * -------Coverage PlayerService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * PlayerService = 40,2%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Player = 14,28571%
	 */
}
