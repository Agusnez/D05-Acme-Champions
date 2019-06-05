
package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import domain.President;
import domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class TeamServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private TeamService			teamService;

	@Autowired
	private PresidentService	presidentService;


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
	 * 2. Name is blank
	 * 3. Name is null
	 * 
	 * c) Sentence coverage
	 * -findAllGamesOrdered():100%
	 * 
	 * d) Data coverage
	 * -Game: 0%
	 */

	@Test
	public void driverCreateTeam() {
		final Object testingData[][] = {
			{
				"name1", "address1", "nameStadium1", "http://url.com", 5, "president4", "1999/09/30", null
			},//1. All fine
			{
				"", "address1", "nameStadium1", "http://url.com", 5, "president4", "1999/09/30", ConstraintViolationException.class
			},//2. Name = blank
			{
				null, "address1", "nameStadium1", "http://url.com", 5, "president4", "1999/09/30", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateTeam((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Integer) testingData[i][4], (String) testingData[i][5],
				this.convertStringToDate((String) testingData[i][6]), (Class<?>) testingData[i][7]);
	}

	protected void templateCreateTeam(final String name, final String address, final String nameStadium, final String badgeUrl, final Integer trackRecord, final String username, final Date establishmentDate, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final Team team = this.teamService.create();

			team.setName(name);
			team.setAddress(address);
			team.setStadiumName(nameStadium);
			team.setBadgeUrl(badgeUrl);
			team.setTrackRecord(trackRecord);
			team.setEstablishmentDate(establishmentDate);

			this.teamService.save(team);
			this.teamService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	@Test
	public void driverEditTeam() {
		final Object testingData[][] = {
			{
				"name1", "address1", "nameStadium1", "http://url.com", 5, "president1", null
			},//1. All fine
			{
				"name1", "address1", "", "http://url.com", 5, "president1", ConstraintViolationException.class
			},//2. Name = blank
			{
				"name1", "address1", null, "http://url.com", 5, "president1", ConstraintViolationException.class
			},//3. Name = null

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditTeam((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (Integer) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateEditTeam(final String name, final String address, final String nameStadium, final String badgeUrl, final Integer trackRecord, final String username, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final President president = this.presidentService.findByPrincipal();

			final Team team = this.teamService.findTeamByPresidentId(president.getId());

			team.setName(name);
			team.setAddress(address);
			team.setStadiumName(nameStadium);
			team.setBadgeUrl(badgeUrl);
			team.setTrackRecord(trackRecord);

			this.teamService.save(team);
			this.teamService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level C) Requirement 11.1: An actor who is authenticated as president must be able to display his/her team.
	 * 
	 * b) Negative cases:
	 * 2. The team does not belongs to the president
	 * 3. Not team
	 * 
	 * c) Sentence coverage
	 * -findTeamByPresidentId(): 100%
	 * -save(): 92.2%
	 * -findPlayersByTeamId(): 100%
	 * -findManagerByTeamId(): 100%
	 * 
	 * d) Data coverage
	 * -Team: 0%
	 */

	@Test
	public void driverDisplayTeam() {
		final Object testingData[][] = {
			{
				"president1", "team1", null
			},//1. All fine
			{
				"president1", "team2", IllegalArgumentException.class
			},//2. The team does not belongs to the president
			{
				"president1", "manager1", IllegalArgumentException.class
			},//3. Not team

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDisplayTeam((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateDisplayTeam(final String username, final String beanTeamExpected, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			this.authenticate(username);

			final President president = this.presidentService.findOne(super.getEntityId(username));

			final Team team = this.teamService.findTeamByPresidentId(president.getId());

			Assert.isTrue(team != null);

			final Team teamExpected = this.teamService.findOne(super.getEntityId(beanTeamExpected));

			Assert.isTrue(team.equals(teamExpected));

			this.teamService.save(team);
			this.teamService.flush();

			this.unauthenticate();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.rollbackTransaction();
		super.checkExceptions(expected, caught);

	}

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

}
