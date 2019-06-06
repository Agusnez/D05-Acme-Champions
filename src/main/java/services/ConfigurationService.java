
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ConfigurationRepository;
import security.Authority;
import domain.Administrator;
import domain.Configuration;
import domain.Game;
import domain.Minutes;

@Service
@Transactional
public class ConfigurationService {

	//Managed Repository

	@Autowired
	private ConfigurationRepository	configurationRepository;

	//Supporting services

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private GameService				gameService;

	@Autowired
	private MinutesService			minutesService;

	@Autowired
	private Validator				validator;


	//Methods

	public Configuration save(final Configuration c) {

		final Administrator admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		final Authority authorityAdmin = new Authority();
		authorityAdmin.setAuthority(Authority.ADMIN);
		Assert.isTrue(admin.getUserAccount().getAuthorities().contains(authorityAdmin));

		final String newBanner = this.checkURL(c.getBanner());
		c.setBanner(newBanner);

		final Configuration configuration = this.configurationRepository.save(c);

		Assert.notNull(configuration);

		return configuration;
	}

	public Configuration findOne(final int configurationId) {

		final Administrator admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		final Authority authorityAdmin = new Authority();
		authorityAdmin.setAuthority(Authority.ADMIN);
		Assert.isTrue(admin.getUserAccount().getAuthorities().contains(authorityAdmin));

		final Configuration configuration = this.configurationRepository.findOne(configurationId);

		Assert.notNull(configuration);

		return configuration;
	}

	public Collection<Configuration> findAll() {

		final Administrator admin = this.administratorService.findByPrincipal();
		Assert.notNull(admin);
		final Authority authorityAdmin = new Authority();
		authorityAdmin.setAuthority(Authority.ADMIN);
		Assert.isTrue(admin.getUserAccount().getAuthorities().contains(authorityAdmin));

		final Collection<Configuration> configurations = this.configurationRepository.findAll();

		Assert.notNull(configurations);

		return configurations;
	}

	public Configuration findConfiguration() {

		Configuration config;
		config = this.configurationRepository.findAll().get(0);
		Assert.notNull(config);
		return config;
	}

	public Boolean spamContent(final String text) {

		Boolean result = false;
		if (!text.isEmpty() && text != null) {
			final Configuration config = this.findConfiguration();

			final Collection<String> spamWords = config.getSpamWords();

			if (!spamWords.isEmpty())
				for (final String word : spamWords)
					if (text.toLowerCase().contains(word.toLowerCase())) {
						result = true;
						break;
					}

		}

		return result;
	}

	public Double goalPrediction(final int teamId) {

		final Collection<Game> localGames = this.gameService.localGamesByTeamId(teamId);
		final Collection<Minutes> localMinutes = new HashSet<Minutes>();
		Integer localGoals = 0;

		for (final Game game : localGames) {

			final Minutes m = this.minutesService.findMinuteByGameId(game.getId());

			if (m != null)
				localMinutes.add(m);
		}

		for (final Minutes minutes : localMinutes)
			localGoals = localGoals + minutes.getHomeScore();

		final Collection<Game> visitorGames = this.gameService.visitorGamesByTeamId(teamId);
		final Collection<Minutes> visitorMinutes = new HashSet<Minutes>();
		Integer visitorGoals = 0;

		for (final Game game : visitorGames) {

			final Minutes m = this.minutesService.findMinuteByGameId(game.getId());

			if (m != null)
				visitorMinutes.add(m);
		}

		for (final Minutes minutes : visitorMinutes)
			visitorGoals = visitorGoals + minutes.getVisitorScore();

		final Double totalGoals = (double) (localGoals + visitorGoals);
		final Double totalGames = (double) (localGames.size() + visitorGames.size());

		Double result = 0.0;

		if (totalGames > 0)
			result = (totalGoals / totalGames);

		return result;
	}

	public Boolean exist(final int id) {

		Boolean result = false;

		final Configuration c = this.configurationRepository.findOne(id);

		if (c != null)
			result = true;

		return result;
	}

	public Configuration reconstruct(final Configuration config, final BindingResult binding) {

		final Configuration bbdd = this.findOne(config.getId());

		final Configuration result = config;
		config.setId(bbdd.getId());
		config.setVersion(config.getVersion());

		this.validator.validate(result, binding);

		return result;
	}

	public void flush() {
		this.configurationRepository.flush();
	}

	public String checkURL(final String url) {
		String newUrl = url;
		if (newUrl != null) {
			newUrl = newUrl.replaceAll("'", "");
			newUrl = newUrl.replaceAll(";", "");
			newUrl = newUrl.replaceAll("\"", "");
			newUrl = newUrl.replaceAll("\\s", "");
		}

		return newUrl;
	}
}
