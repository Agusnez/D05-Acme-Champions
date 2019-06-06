
package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Player;
import domain.Training;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class TrainingServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private TrainingService	trainingService;

	@Autowired
	private PlayerService	playerService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a manager must be able to: Manage their trainings: Create training
	 * 
	 * b) Negative cases:
	 * 2. Player is trying to create a training
	 * 3. End date is past
	 * 
	 * c) Sentence coverage
	 * -create():100%
	 * -save():97.8%
	 * d) Data coverage
	 * -Training: 0%
	 */
	@Test
	public void driverCreateTraining() {
		final Object testingData[][] = {
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "Sesion prepartido", "manager1", null
			},//1. All fine
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "Sesion prepartido", "player1", IllegalArgumentException.class
			},//2. Player is trying to create a training
			{
				"2019/09/24 10:00", "2018/09/24 15:00", "Sevilla", "Sesion prepartido", "manager1", DataIntegrityViolationException.class
			},//3. End date is past

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateTraining(this.convertStringToDate((String) testingData[i][0]), this.convertStringToDate((String) testingData[i][1]), (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4],
				(Class<?>) testingData[i][5]);
	}

	protected void templateCreateTraining(final Date start, final Date end, final String place, final String description, final String username, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Training training = this.trainingService.create();

			training.setDescription(description);
			training.setEndingDate(end);
			training.setPlace(place);
			training.setStartDate(start);
			training.setDescription(description);

			this.trainingService.save(training);
			this.trainingService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a manager must be able to: Manage their trainings: Display training
	 * 
	 * b) Negative cases:
	 * 2. Not training
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
				"training1", null
			},//1. All fine
			{
				"player2", IllegalArgumentException.class
			},//2. Not training
			{
				null, IllegalArgumentException.class
			},//3. Null object

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplayTraining((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateDisplayTraining(final String trainingId, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			Assert.isTrue(trainingId != null);

			final Training training = this.trainingService.findOne(super.getEntityId(trainingId));

			Assert.isTrue(training != null);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}
	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a manager must be able to: Manage their trainings: List trainings
	 * 
	 * b) Negative cases:
	 * 2.The number of trainings is incorrect
	 * 3.Not manager
	 * 
	 * c) Sentence coverage
	 * -findTrainingsByManagerId():100%
	 * 
	 * d) Data coverage
	 * -Training: 0%
	 */

	@Test
	public void driverListTraining() {
		final Object testingData[][] = {
			{
				"manager1", 1, null
			},//1. All fine
			{
				"manager1", 6, IllegalArgumentException.class
			},//2. The number of trainings is incorrect
			{
				"president1", 1, IllegalArgumentException.class
			},//3. Not manager

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListTraining((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateListTraining(final String manager, final Integer number, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();

			final Collection<Training> trainings = this.trainingService.findTrainingsByManagerId((super.getEntityId(manager)));

			Assert.isTrue(trainings.size() == number);
		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a manager must be able to: Manage their trainings: Delete trainings
	 * 
	 * b) Negative cases:
	 * 2. Not training
	 * 3.The authority is not correct
	 * 
	 * c) Sentence coverage
	 * -findOne():100%
	 * -delete():100%
	 * d) Data coverage
	 * -Training: 0%
	 */
	@Test
	public void driverDeleteTraining() {
		final Object testingData[][] = {
			{
				"manager1", "training1", null
			},//1. All fine
			{
				"manager1", "player2", IllegalArgumentException.class
			},//2. Not training
			{
				"player1", "training1", IllegalArgumentException.class
			},//2. The authority is not correct

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteTraining((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteTraining(final String username, final String trainingToDelete, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();

			this.authenticate(username);

			final Training training = this.trainingService.findOne(super.getEntityId(trainingToDelete));

			this.trainingService.delete(training);
		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a manager must be able to: Manage their trainings: Edit trainings
	 * 
	 * b) Negative cases:
	 * 2. The start date is past
	 * 3. The end date is past
	 * 4. The end date is before start date
	 * 5. The authority is incorrect
	 * 6. The description is not Safe HTML
	 * 7. The place is not Safe HTML
	 * 8. The description is blank
	 * 9. The place is blank
	 * 
	 * c) Sentence coverage
	 * -findOne():100%
	 * -save():97,8%
	 * d) Data coverage
	 * -Training: 100%
	 */
	@Test
	public void driverEditTraining() {
		final Object testingData[][] = {
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "Sesion prepartido", "manager2", "training2", null
			},//1. All fine
			{
				"2013/09/24 10:00", "2019/09/24 15:00", "Sevilla", "Sesion prepartido", "manager2", "training2", DataIntegrityViolationException.class
			},//2. The start date is past
			{
				"2019/09/24 10:00", "2014/09/24 15:00", "Sevilla", "Sesion prepartido", "manager2", "training2", DataIntegrityViolationException.class
			},//3. The end date is past
			{
				"2019/09/24 10:00", "2019/09/23 15:00", "Sevilla", "Sesion prepartido", "manager2", "training2", DataIntegrityViolationException.class
			},//4. The end date is before start date
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "Sesion prepartido", "player1", "training2", IllegalArgumentException.class
			},//5. The authority  is incorrect
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "<script>alert('hola')</script>", "manager2", "training2", ConstraintViolationException.class
			},//6. The description is not Safe HTML
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "<script>alert('hola')</script>", "Sesion prepartido", "manager2", "training2", ConstraintViolationException.class
			},//7. The place is not Safe HTML
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "Sevilla", "", "manager2", "training2", ConstraintViolationException.class
			},//8. The description is blank
			{
				"2019/09/24 10:00", "2019/09/24 15:00", "", "Sesion prepartido", "manager2", "training2", ConstraintViolationException.class
			},//9. The place is blank

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditTraining(this.convertStringToDate((String) testingData[i][0]), this.convertStringToDate((String) testingData[i][1]), (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4],
				(String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateEditTraining(final Date start, final Date end, final String place, final String description, final String username, final String trainingToEdit, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Training training = this.trainingService.findOne(super.getEntityId(trainingToEdit));

			training.setDescription(description);
			training.setEndingDate(end);
			training.setPlace(place);
			training.setStartDate(start);
			training.setDescription(description);

			this.trainingService.save(training);
			this.trainingService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.2: An actor who is authenticated as a manager must be able to: Manage their trainings: Train the team in which they are. That is, assign each player to a
	 * training.
	 * 
	 * b) Negative cases:
	 * 2. President cannot add player to training
	 * 3. The manager is not the correct manager of the training
	 * 
	 * c) Sentence coverage
	 * -findOne():100%
	 * -addPlayerToTraining():96.5%
	 * d) Data coverage
	 * -Training: 0%
	 */
	@Test
	public void driverAddPlayerToTraining() {
		final Object testingData[][] = {
			{
				"manager1", "training1", "player5", null
			},//1. All fine
			{
				"president1", "training2", "player13", IllegalArgumentException.class
			},//2. President cannot add player to training
			{
				"manager1", "training2", "player13", IllegalArgumentException.class
			},//3. The manager is not the correct manager of the training

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateAddPlayerToTraining((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}
	protected void templateAddPlayerToTraining(final String username, final String training, final String player, final Class<?> expected) {
		Class<?> caught;

		caught = null;
		try {
			this.startTransaction();
			this.authenticate(username);

			final Training trainingBBDD = this.trainingService.findOne(super.getEntityId(training));
			final Player playerBBDD = this.playerService.findOne(super.getEntityId(player));
			this.trainingService.addPlayerToTraining(playerBBDD, trainingBBDD);

			this.trainingService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();

		}
		this.unauthenticate();
		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	//Methods

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

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 12.1: An actor who is authenticated as a player must be able to: List all the trainings that the manager has assign them.
	 * 
	 * b) Negative cases:
	 * 2.Wrong number of trainings
	 * 3.Not player
	 * 
	 * c) Sentence coverage
	 * -findTrainingsByPlayerId():100%
	 * 
	 * d) Data coverage
	 */

	@Test
	public void driverListTrainingOfPlayer() {
		final Object testingData[][] = {

			{
				"player1", 1, null
			},//1. All fine
			{
				"player1", 10, IllegalArgumentException.class
			},//2. Wrong number of trainings
			{
				"president1", 10, IllegalArgumentException.class
			},//3. Not player

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListTrainingOfPlayer((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateListTrainingOfPlayer(final String username, final Integer expectedInt, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			super.authenticate(username);

			final int id = this.playerService.findByPrincipal().getId();

			final Integer result = this.trainingService.findTrainingsByPlayerId(id).size();
			Assert.isTrue(expectedInt == result);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage TrainingService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * TrainingService = 74,4%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Sponsor = 0%
	 */

}
