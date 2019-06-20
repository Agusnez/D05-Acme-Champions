
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

import utilities.AbstractTest;
import domain.History;
import domain.SportRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class SportRecordServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private SportRecordService	sportRecordService;

	@Autowired
	private HistoryService		historyService;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to create her/his sport record.
	 * 
	 * b) Negative cases:
	 * 2. SportName = null
	 * 3. SportName = blank
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 95,6%
	 * 
	 * d) Data coverage
	 * -SportRecord: 25%
	 */

	@Test
	public void driverCreateSportRecord() {
		final Object testingData[][] = {
			{
				"player1", "1998/06/29", "2000/06/29", "Fútbol", null
			},//1. All fine
			{
				"player1", "1998/06/29", "2000/06/29", null, ConstraintViolationException.class
			},//2. SportName = null
			{
				"player1", "1998/06/29", "2000/06/29", "		", ConstraintViolationException.class
			},//3. SportName = blank
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateSportRecord((String) testingData[i][0], this.convertStringToDate((String) testingData[i][1]), this.convertStringToDate((String) testingData[i][2]), (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}
	protected void templateCreateSportRecord(final String username, final Date startDate, final Date endDate, final String sportName, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final SportRecord record = this.sportRecordService.create();

			record.setStartDate(startDate);
			record.setEndDate(endDate);
			record.setSportName(sportName);
			record.setTeamSport(true);

			this.sportRecordService.save(record);
			this.sportRecordService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to edit her/his sport record.
	 * 
	 * b) Negative cases:
	 * 2. Start date = null
	 * 3. Start date > End date
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 100%
	 * 
	 * d) Data coverage
	 * -SportRecord: 25%
	 */

	@Test
	public void drivereEditSportRecord() {
		final Object testingData[][] = {
			{
				"player1", "1998/06/29", "2000/06/29", "Fútbol", "sportRecord1", null
			},//1. All fine
			{
				"player1", null, "2000/06/29", "Fútbol", "sportRecord1", ConstraintViolationException.class
			},//2. Start date = null
			{
				"player1", "2001/06/29", "2000/06/29", "Fútbol", "sportRecord1", IllegalArgumentException.class
			},//3. Start date > End date

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditSportRecord((String) testingData[i][0], this.convertStringToDate((String) testingData[i][1]), this.convertStringToDate((String) testingData[i][2]), (String) testingData[i][3], (String) testingData[i][4],
				(Class<?>) testingData[i][5]);
	}
	protected void templateEditSportRecord(final String username, final Date startDate, final Date endDate, final String sportName, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final SportRecord record = this.sportRecordService.findOne(super.getEntityId(bean));

			record.setStartDate(startDate);
			record.setEndDate(endDate);
			record.setSportName(sportName);
			record.setTeamSport(true);

			this.sportRecordService.save(record);
			this.sportRecordService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to delete her/his sport record.
	 * 
	 * b) Negative cases:
	 * 2. Invalid authority
	 * 3. Not sport record
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -delete(): 97%
	 * 
	 * d) Data coverage
	 * -SportRecord: 0%
	 */

	@Test
	public void driverDeleteSportRecord() {
		final Object testingData[][] = {
			{
				"player1", "sportRecord1", null
			},//1. All fine
			{
				"manager1", "sportRecord1", IllegalArgumentException.class
			},//2. Invalid authority
			{
				"player1", "manager1", IllegalArgumentException.class
			},//3. Not sport record
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteSportRecord((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}
	protected void templateDeleteSportRecord(final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final SportRecord record = this.sportRecordService.findOne(super.getEntityId(bean));

			if (record != null) {
				final History history = this.historyService.historyPerSportRecordId(record.getId());
				history.getSportRecords().remove(record);
				this.historyService.save(history);
			}

			this.sportRecordService.delete(record);
			this.sportRecordService.flush();

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

	protected Double convertStringToDouble(final String doubleString) {
		Double result = null;

		if (doubleString != null)
			result = Double.valueOf(doubleString);

		return result;
	}

	/*
	 * -------Coverage SportRecordService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * SportRecordService = 67,8%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * SportRecord = 50%%
	 */
}
