
package services;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.HiringRepository;
import security.Authority;
import domain.Actor;
import domain.Hiring;
import domain.Manager;
import domain.Team;
import forms.HiringForm;

@Service
@Transactional
public class HiringService {

	// Managed Repository ------------------------
	@Autowired
	private HiringRepository	hiringRepository;

	// Suporting services ------------------------

	@Autowired
	private ActorService		actorService;

	@Autowired
	private ManagerService		managerService;

	@Autowired
	private Validator			validator;

	@Autowired
	private PresidentService	presidentService;

	@Autowired
	private TeamService			teamService;


	public HiringForm create(final Integer managerId) {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.PRESIDENT);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		HiringForm result;
		result = new HiringForm();

		result.setManagerId(managerId);

		return result;

	}

	public Collection<Hiring> findAll() {

		Collection<Hiring> result;
		result = this.hiringRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Hiring findOne(final int hiringId) {

		Assert.notNull(hiringId);
		Hiring result;
		result = this.hiringRepository.findOne(hiringId);
		return result;
	}

	public Hiring save(final Hiring hiring) {

		Assert.notNull(hiring);
		Hiring result;

		if (hiring.getId() == 0) {

			final Actor actor = this.actorService.findByPrincipal();
			Assert.notNull(actor);
			final Authority authority = new Authority();
			authority.setAuthority(Authority.PRESIDENT);
			Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

			hiring.setStatus("PENDING");

			result = this.hiringRepository.save(hiring);

		} else {

			final Actor actor = this.actorService.findByPrincipal();
			Assert.notNull(actor);
			final Authority authority2 = new Authority();
			authority2.setAuthority(Authority.MANAGER);
			final Authority authority3 = new Authority();
			authority3.setAuthority(Authority.PRESIDENT);

			Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority2)) || (actor.getUserAccount().getAuthorities().contains(authority3)));

			result = this.hiringRepository.save(hiring);

		}
		return result;
	}
	public Hiring reconstruct(final HiringForm form, final BindingResult binding) {

		final Hiring hiring = new Hiring();

		if (form.getId() == 0) {

			hiring.setManager(this.managerService.findOne(form.getManagerId()));
			hiring.setMandatoryComment(form.getMandatoryComment());
			hiring.setPresident(this.presidentService.findByPrincipal());
			hiring.setPrice(form.getPrice());
			hiring.setStatus("PENDING");

		} else {

			final Hiring oldOne = this.hiringRepository.findOne(form.getId());

			hiring.setId(oldOne.getId());
			hiring.setManager(oldOne.getManager());
			hiring.setMandatoryComment(form.getMandatoryComment());
			hiring.setPresident(oldOne.getPresident());
			hiring.setPrice(oldOne.getPrice());
			hiring.setVersion(oldOne.getVersion());
			hiring.setStatus(oldOne.getStatus());

		}

		this.validator.validate(hiring, binding);

		return hiring;

	}

	public Collection<Hiring> findByPresident(final int id) {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.PRESIDENT);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		final Team team = this.teamService.findByPresidentId(id);

		final Manager manager = this.teamService.findManagerByTeamId((team.getId()));

		Collection<Hiring> res = null;

		if (manager != null)
			res = this.hiringRepository.findByManagerIdPresident(manager.getId());

		return res;
	}

	public Boolean exist(final int hiringId) {

		Boolean res = false;

		final Hiring hiring = this.hiringRepository.findOne(hiringId);

		if (hiring != null)
			res = true;

		return res;
	}

	public HiringForm editForm(final Hiring hiring) {

		final HiringForm res = new HiringForm();

		res.setId(hiring.getId());
		res.setManagerId(hiring.getManager().getId());
		res.setMandatoryComment(hiring.getMandatoryComment());
		res.setPrice(hiring.getPrice());
		res.setVersion(hiring.getVersion());

		return res;
	}

	public Collection<Hiring> findByManager(final int id) {

		final Collection<Hiring> res = this.hiringRepository.findByManagerId(id);

		return res;
	}

	public void delete(final Hiring hiring) {

		Assert.notNull(hiring);
		Assert.isTrue(hiring.getId() != 0);

		this.hiringRepository.delete(hiring);

	}

	public Hiring findHiringOfPresidentAndManager(final int presidentId, final int managerId) {
		return this.hiringRepository.findHiringOfPresidentAndManager(presidentId, managerId);
	}

	public Collection<Hiring> findAllByManager(final int id) {

		final Collection<Hiring> res = this.hiringRepository.findAllByManager(id);

		return res;
	}

	public Collection<Hiring> findByPresidentDown(final int presidentId) {
		final Team team = this.teamService.findByPresidentId(presidentId);
		Collection<Hiring> res = new HashSet<Hiring>();

		if (team != null) {
			final Manager manager = this.teamService.findManagerByTeamId((team.getId()));
			if (manager != null)
				res = this.hiringRepository.findByManagerIdPresident(manager.getId());
		}

		return res;
	}
}
