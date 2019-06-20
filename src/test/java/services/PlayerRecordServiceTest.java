
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
import domain.PlayerRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class PlayerRecordServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private PlayerRecordService	playerRecordService;

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
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to create her/his player record.
	 * 
	 * b) Negative cases:
	 * 2. Salary = null
	 * 3. Salary < 0.0
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * -save(): 95,6%
	 * 
	 * d) Data coverage
	 * -PlayerRecord: 25%
	 */

	@Test
	public void driverCreatePlayerRecord() {
		final Object testingData[][] = {
			{
				"player1", "1998/06/29", "2000/06/29", "22000.0", "7", null
			},//1. All fine
			{
				"player1", "1998/06/29", "2000/06/29", null, "7", ConstraintViolationException.class
			},//2. Salary = null
			{
				"player1", "1998/06/29", "2000/06/29", "-22000.0", "7", ConstraintViolationException.class
			},//3. Salary < 0.0
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreatePlayerRecord((String) testingData[i][0], this.convertStringToDate((String) testingData[i][1]), this.convertStringToDate((String) testingData[i][2]), this.convertStringToDouble((String) testingData[i][3]),
				Integer.valueOf((String) testingData[i][4]), (Class<?>) testingData[i][5]);
	}

	protected void templateCreatePlayerRecord(final String username, final Date startDate, final Date endDate, final Double salary, final Integer squadNumber, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final PlayerRecord record = this.playerRecordService.create();

			record.setStartDate(startDate);
			record.setEndDate(endDate);
			record.setSalary(salary);
			record.setSquadNumber(squadNumber);

			this.playerRecordService.save(record);
			this.playerRecordService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to edit her/his player record.
	 * 
	 * b) Negative cases:
	 * 2. SquadNumber < 0
	 * 3. SquadNumber > 99
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -save(): 95,6%
	 * 
	 * d) Data coverage
	 * -PlayerRecord: 25%
	 */

	@Test
	public void driverEditPlayerRecord() {
		final Object testingData[][] = {
			{
				"player1", "1998/06/29", "2000/06/29", "22000.0", "7", "playerRecord1", null
			},//1. All fine
			{
				"player1", "1998/06/29", "2000/06/29", "22000.0", "-7", "playerRecord1", ConstraintViolationException.class
			},//2. SquadNumber < 0
			{
				"player1", "1998/06/29", "2000/06/29", "22000.0", "700", "playerRecord1", ConstraintViolationException.class
			},//3. SquadNumber > 99
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditPlayerRecord((String) testingData[i][0], this.convertStringToDate((String) testingData[i][1]), this.convertStringToDate((String) testingData[i][2]), this.convertStringToDouble((String) testingData[i][3]),
				this.convertStringToInteger((String) testingData[i][4]), (String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateEditPlayerRecord(final String username, final Date startDate, final Date endDate, final Double salary, final Integer squadNumber, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final PlayerRecord record = this.playerRecordService.findOne(super.getEntityId(bean));

			record.setStartDate(startDate);
			record.setEndDate(endDate);
			record.setSalary(salary);
			record.setSquadNumber(squadNumber);

			this.playerRecordService.save(record);
			this.playerRecordService.flush();

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
	 * ACME.CHAMPIONS
	 * a)Requirement 29.3: An actor who is authenticated as player must be able to delete her/his player record.
	 * 
	 * b) Negative cases:
	 * 2. Invalid authority
	 * 3. Not player record
	 * 
	 * c) Sentence coverage
	 * -findOne(): 100%
	 * -delete(): 97%
	 * 
	 * d) Data coverage
	 * -PlayerRecord: 0%
	 */

	@Test
	public void driverDeletePlayerRecord() {
		final Object testingData[][] = {
			{
				"player1", "playerRecord1", null
			},//1. All fine
			{
				"manager1", "playerRecord1", IllegalArgumentException.class
			},//2. Invalid authority
			{
				"player1", "manager1", IllegalArgumentException.class
			},//3. Not player record
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeletePlayerRecord((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}
	protected void templateDeletePlayerRecord(final String username, final String bean, final Class<?> expected) {

		Class<?> caught;

		caught = null;
		try {

			this.startTransaction();

			super.authenticate(username);

			final PlayerRecord record = this.playerRecordService.findOne(super.getEntityId(bean));

			if (record != null) {
				final History history = this.historyService.historyPerPlayerRecordId(record.getId());
				history.getPlayerRecords().remove(record);
				this.historyService.save(history);
			}

			this.playerRecordService.delete(record);
			this.playerRecordService.flush();

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}
	//Methods 
	protected Double convertStringToDouble(final String doubleString) {
		Double result = null;

		if (doubleString != null)
			result = Double.valueOf(doubleString);

		return result;
	}

	protected Integer convertStringToInteger(final String integerString) {
		Integer result = null;

		if (integerString != null)
			result = Integer.valueOf(integerString);

		return result;
	}

	/*
	 * -------Coverage PlayerRecordService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * PlayerRecordService = 66,7%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * PlayerRecord = 50%%
	 */
}
