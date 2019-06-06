
package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import utilities.AbstractTest;
import domain.Competition;
import forms.CompetitionForm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class CompetitionServiceTest extends AbstractTest {

	//The SUT----------------------------------------------------
	@Autowired
	private CompetitionService	competitionService;

	@Autowired
	private FederationService	federationService;

	@Autowired
	private FormatService		formatService;

	@Autowired
	private Validator			validator;


	/*
	 * ----CALCULATE COVERAGE----
	 * The previous delivery, we calculate it manually. In this one instead we are using the plugin called EclEmma,
	 * with which we can automatically calculate the percentage.
	 * 
	 * Each of the test have their result just before them, and the coverage of the complete test is shown at the end of the document.
	 */

	/*
	 * ACME.CHAMPIONS
	 * a)(Level A) Requirement 44.1: An actor who is authenticated as a federation must be able to: Create competitions
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. No loged actor
	 * 
	 * c) Sentence coverage
	 * -create(): 100%
	 * 
	 * d) Data coverage
	 * -Competition: 0%
	 */

	@Test
	public void driverCreate() {

		final Object testingData[][] = {

			{
				"federation1", null
			}, //1. All fine
			{
				"administrator1", IllegalArgumentException.class
			}, //2. Wrong authority
			{
				null, IllegalArgumentException.class
			}, //3. No loged actor

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateCreate(final String actorBean, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final CompetitionForm compeititon = this.competitionService.create();

			Assert.notNull(compeititon);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level A) Requirement 44.2: An actor who is authenticated as a federation must be able to: List competitions
	 * 
	 * b) Negative cases:
	 * 2. Wrong authority
	 * 3. Wrong authority
	 * 
	 * c) Sentence coverage
	 * -findByFederation(): 100%
	 * 
	 * d) Data coverage
	 * -Competition: 0%
	 */

	@Test
	public void driverList() {

		final Object testingData[][] = {

			{
				"federation1", null
			}, //1. All fine
			{
				"administrator1", IllegalArgumentException.class
			}, //2. Wrong authority
			{
				"referee1", IllegalArgumentException.class
			}, //3. Wrong authority

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateList((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	protected void templateList(final String actorBean, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final Collection<Competition> competition = this.competitionService.findByFederationId(super.getEntityId(actorBean));

			Assert.notNull(competition);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * ACME.CHAMPIONS
	 * a)(Level A) Requirement 44.1: An actor who is authenticated as a federation must be able to: Create competitions
	 * 
	 * b) Negative cases:
	 * 2. blank field
	 * 3. javascript
	 * 
	 * c) Sentence coverage
	 * -save(): 11,9%
	 * 
	 * d) Data coverage
	 * -Competition: 25%
	 */

	@Test
	public void driverSave() {

		final Object testingData[][] = {

			{
				"federation1", "format1", "test", "2020/12/12 12:00:00", null
			}, //1. All fine
			{
				"federation1", "format1", "", "2020/12/12 12:00:00", NullPointerException.class
			}, //2. blank field
			{
				"federation1", "format1", "<script>alert('test');</script>", "2020/12/12 12:00:00", NullPointerException.class
			}, //3. javascript

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateSave((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], this.convertStringToDate((String) testingData[i][3]), (Class<?>) testingData[i][4]);

	}

	protected void templateSave(final String actorBean, final String format, final String nameTrophy, final Date startDate, final Class<?> expected) {

		Class<?> caught;

		caught = null;

		try {

			this.startTransaction();

			super.authenticate(actorBean);

			final Competition res = new Competition();

			res.setFormat(this.formatService.findOne(super.getEntityId(format)));
			res.setNameTrophy(nameTrophy);
			res.setStartDate(startDate);
			res.setClosed(false);
			res.setFederation(this.federationService.findByPrincipal());

			final BindingResult binding = null;

			this.validator.validate(res, binding);

			if (binding != null)
				Assert.isTrue(!binding.hasErrors());

			final Competition saved = this.competitionService.save(res);

			Assert.notNull(saved);

		} catch (final Throwable oops) {
			caught = oops.getClass();
		}

		this.unauthenticate();

		this.rollbackTransaction();

		super.checkExceptions(expected, caught);

	}

	/*
	 * -------Coverage CompetitionService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * CompetitionService = 10,7%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Competition = 25%
	 */

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
	 * -------Coverage CompetitionService-------
	 * 
	 * ----TOTAL SENTENCE COVERAGE:
	 * CompetitionService = 12%
	 * 
	 * ----TOTAL DATA COVERAGE:
	 * Competition = 0%
	 */
}
