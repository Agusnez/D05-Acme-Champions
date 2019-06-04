
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.FormatRepository;
import security.Authority;
import domain.Actor;
import domain.Competition;
import domain.Federation;
import domain.Format;

@Service
@Transactional
public class FormatService {

	// Managed Repository --------------------
	@Autowired
	private FormatRepository	formatRepository;

	// Supporting Services -------------------
	@Autowired
	private ActorService		actorService;

	@Autowired
	private FederationService	federationService;

	@Autowired
	private Validator			validator;

	@Autowired
	private CompetitionService	competitionService;


	// Simple CRUD methods

	public Format create() {

		final Format result = new Format();

		return result;

	}

	public Collection<Format> findAll() {

		final Collection<Format> result = this.formatRepository.findAll();
		Assert.notNull(result);
		return result;

	}

	public Format findOne(final int formatId) {

		final Format result = this.formatRepository.findOne(formatId);
		return result;

	}

	public Format save(final Format format) {
		Assert.notNull(format);

		final Authority authFederation = new Authority();
		authFederation.setAuthority(Authority.FEDERATION);

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);

		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authFederation));

		if (format.getId() != 0) {
			Assert.isTrue(actor.getId() == format.getFederation().getId());
			final Collection<Competition> competitionsByFormat = this.competitionService.findByFormatId(format.getId());
			competitionsByFormat.remove(null);
			Assert.isTrue(competitionsByFormat.isEmpty());
		}

		final Format result = this.formatRepository.save(format);

		return result;

	}
	public void delete(final Format format) {
		Assert.notNull(format);

		final Authority authFederation = new Authority();
		authFederation.setAuthority(Authority.FEDERATION);

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);

		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authFederation));

		Assert.isTrue(format.getFederation().getId() == actor.getId());

		this.formatRepository.delete(format);

	}

	// Methods -------------------------------

	public Format reconstruct(final Format format, final BindingResult binding) {

		// Hay que estar logeado
		final Actor actor = this.actorService.findByPrincipal();
		final Federation fede = this.federationService.findByUserAccount(actor.getUserAccount());
		Assert.notNull(actor);

		Assert.notNull(format);

		format.setType(format.getType());

		if (format.getMaximumTeams() != null && format.getMinimumTeams() != null)
			if (format.getMinimumTeams() <= format.getMaximumTeams()) {
				format.setMinimumTeams(format.getMinimumTeams());
				format.setMaximumTeams(format.getMaximumTeams());
			} else {
				final Integer actualMinimum = format.getMaximumTeams();
				final Integer actualMaximum = format.getMinimumTeams();
				format.setMinimumTeams(actualMinimum);
				format.setMaximumTeams(actualMaximum);
			}

		if (format.getId() == 0)
			format.setFederation(fede);
		else {
			final Format formatBBDD = this.findOne(format.getId());
			format.setFederation(formatBBDD.getFederation());
		}

		this.validator.validate(format, binding);

		return format;
	}

	public Collection<Format> findFormatByFederationId(final int federationId) {
		return this.formatRepository.findFormatByFederationId(federationId);
	}

	public Boolean exist(final int formatId) {

		Boolean res = false;

		final Format format = this.formatRepository.findOne(formatId);

		if (format != null)
			res = true;

		return res;
	}

	public Boolean security(final int formatId) {

		Boolean res = false;

		final Format format = this.formatRepository.findOne(formatId);
		final Actor actor = this.actorService.findByPrincipal();

		if (format.getFederation().getId() == actor.getId())
			res = true;

		return res;
	}

}
