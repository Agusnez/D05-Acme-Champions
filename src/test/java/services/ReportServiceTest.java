
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Manager;
import domain.Player;
import domain.Report;
import domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ReportServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private ReportService	reportService;

	@Autowired
	private PlayerService	playerService;

	@Autowired
	private ManagerService	managerService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 32.1: An actor who is authenticated as a manager must be able to create a report about a player.
	 * 
	 * b) Negative cases:
	 * 2. Description = blank
	 * 3. Description = null
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -Report: 33,33333%
	 */

	@Test
	public void driverCreateReport() {
		final Object testingData[][] = {
			{
				"2019/04/23", "example", "player1", null
			},//1. All fine
			{
				"2019/04/23", "		", "player1", ConstraintViolationException.class
			},//2. Description = blank
			{
				"2019/04/23", null, "player1", ConstraintViolationException.class
			},//3. Description = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateReport(this.convertStringToDate((String) testingData[i][0]), (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateCreateReport(final Date moment, final String description, final String beanPlayer, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			final Report report = this.reportService.create();

			final Player player = this.playerService.findOne(super.getEntityId(beanPlayer));

			report.setMoment(moment);
			report.setDescription(description);
			report.setPlayer(player);

			this.reportService.save(report);
			this.reportService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 32.1: An actor who is authenticated as a manager must be able to delete a report about a player.
	 * 
	 * b) Negative cases:
	 * 2. Not report
	 * 3. Invalid authority
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -delete(): 97%
	 * 
	 * d) Data coverage
	 * -Report: 0%
	 */

	@Test
	public void driverDeleteReport() {
		final Object testingData[][] = {
			{
				"report1", "manager1", null
			},//1. All fine
			{
				"player1", "manager1", IllegalArgumentException.class
			},//2. Not report
			{
				"report1", "president1", IllegalArgumentException.class
			},//3. Invalid authority

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteReport((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDeleteReport(final String bean, final String username, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Report report = this.reportService.findOne(super.getEntityId(bean));

			this.reportService.delete(report);
			this.reportService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level B) Requirement 32.1: An actor who is authenticated as a manager must be able to list the reports about a player.
	 * 
	 * b) Negative cases:
	 * 2. Not report
	 * 3. Invalid authority
	 * 
	 * c) Sentence coverage
	 * -findByTeamId(): 100%
	 * 
	 * d) Data coverage
	 * -Report: 0%
	 */

	@Test
	public void driverListReport() {
		final Object testingData[][] = {
			{
				"manager1", "manager1", 1, null
			},//1. All fine
			{
				"manager1", "manager1", 777, IllegalArgumentException.class
			},//2. Not expected number
			{
				"manager1", "president1", 1, IllegalArgumentException.class
			},//3. Invalid authority

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListReport((String) testingData[i][0], (String) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateListReport(final String beanManager, final String username, final Integer numberExpected, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Manager manager = this.managerService.findOne(super.getEntityId(beanManager));

			final Team team = manager.getTeam();

			final Collection<Report> reports = this.reportService.findByTeamId(team.getId());

			Assert.isTrue(reports.size() == numberExpected);

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
			final DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			try {
				date = df.parse(dateString);
			} catch (final Exception ex) {
				System.out.println(ex);
			}
		}

		return date;
	}

	/*
	 * -------Coverage ReportService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * ReportService = 51,4%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Report = 33,33333%
	 */
}
