
package controllers.actor;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import security.Authority;
import services.ActorService;
import services.CompetitionService;
import services.FederationService;
import services.FinderService;
import services.FormatService;
import services.GameService;
import services.HiringService;
import services.HistoryService;
import services.ManagerService;
import services.MessageService;
import services.MinutesService;
import services.PersonalDataService;
import services.PlayerRecordService;
import services.PlayerService;
import services.PresidentService;
import services.RefereeService;
import services.ReportService;
import services.SigningService;
import services.SponsorService;
import services.SponsorshipService;
import services.SportRecordService;
import services.StatisticalDataService;
import services.TeamService;
import services.TrainingService;
import controllers.AbstractController;
import domain.Actor;
import domain.Competition;
import domain.Federation;
import domain.Format;
import domain.Game;
import domain.Hiring;
import domain.History;
import domain.Manager;
import domain.Message;
import domain.Minutes;
import domain.Player;
import domain.PlayerRecord;
import domain.President;
import domain.Referee;
import domain.Signing;
import domain.Sponsor;
import domain.Sponsorship;
import domain.SportRecord;
import domain.StatisticalData;
import domain.Team;
import domain.Training;

@Controller
@RequestMapping("/data")
public class DownloadDataActorController extends AbstractController {

	@Autowired
	private MessageService			messageService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private FederationService		federationService;
	@Autowired
	private RefereeService			refereeService;
	@Autowired
	private SponsorService			sponsorService;
	@Autowired
	private PresidentService		presidentService;
	@Autowired
	private ManagerService			managerService;
	@Autowired
	private PlayerService			playerService;

	@Autowired
	private CompetitionService		competitionService;
	@Autowired
	private FormatService			formatService;
	@Autowired
	private GameService				gameService;
	@Autowired
	private MinutesService			minutesService;
	@Autowired
	private SponsorshipService		sponsorshipService;
	@Autowired
	private TeamService				teamService;
	@Autowired
	private SigningService			signingService;
	@Autowired
	private HiringService			hiringService;
	@Autowired
	private TrainingService			trainingService;
	@Autowired
	private FinderService			finderService;
	@Autowired
	private StatisticalDataService	statisticalDataService;
	@Autowired
	private ReportService			reportService;
	@Autowired
	private HistoryService			historyService;
	@Autowired
	private PersonalDataService		personalDataService;
	@Autowired
	private PlayerRecordService		playerRecordService;
	@Autowired
	private SportRecordService		sportRecordService;


	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public void test(final HttpSession session, final HttpServletResponse response) throws IOException {

		final String language = LocaleContextHolder.getLocale().getLanguage();

		if (language == "en") {
			String myString = "Below these lines you can find all the user data we have at Acme-Champions:\r\n";

			final Actor a = this.actorService.findByPrincipal();
			final Collection<Message> msgs = this.messageService.messagePerActor(a.getId());

			myString += "\r\n\r\n";

			myString += a.getUserAccount().getAuthorities();

			myString += "\r\n Full name:" + a.getName() + " " + a.getSurnames() + "\r\n Address:" + a.getAddress() + "\r\n Email:" + a.getEmail() + "\r\n Phone:" + a.getPhone() + "\r\n Photo:" + a.getPhoto();
			myString += "\r\n\r\n";
			myString += "Messages:\r\n";
			for (final Message msg : msgs)
				myString += "Sender: " + msg.getSender().getName() + " " + msg.getSender().getSurnames() + " Recipient: " + msg.getRecipient().getName() + " " + msg.getRecipient().getSurnames() + " Moment: " + msg.getMoment() + " Subject: "
					+ msg.getSubject() + " Body: " + msg.getBody() + " Tags: " + msg.getTags() + "\r\n";
			myString += "\r\n\r\n";

			final Authority fede = new Authority();
			fede.setAuthority(Authority.FEDERATION);
			if (a.getUserAccount().getAuthorities().contains(fede)) {
				final Federation f = this.federationService.findByUserAccount(a.getUserAccount());
				final Collection<Competition> cs = this.competitionService.findByFederationId(f.getId());
				final Collection<Format> fs = this.formatService.findFormatByFederationId(f.getId());
				myString += "Competitions:\r\n";
				for (final Competition c : cs)
					myString += "Trophy name:" + c.getNameTrophy() + " Starts:" + c.getStartDate() + " Ends:" + c.getEndDate() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Formats:\r\n";
				for (final Format fo : fs)
					myString += "Type:" + fo.getType() + " Maximum teams:" + fo.getMaximumTeams() + " Minimum teams:" + fo.getMinimumTeams() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority r = new Authority();
			r.setAuthority(Authority.REFEREE);
			if (a.getUserAccount().getAuthorities().contains(r)) {
				final Referee re = this.refereeService.findByUserAccount(a.getUserAccount());
				final Collection<Game> gs = this.gameService.findGameByRefereeId(re.getId());

				myString += "Games:\r\n";
				for (final Game g : gs) {
					myString += "Place: " + g.getPlace() + " Starts: " + g.getGameDate() + " Local team: " + g.getHomeTeam().getName() + " Visitor team: " + g.getVisitorTeam().getName() + "\r\n";
					final Minutes m = this.minutesService.findMinuteByGameId(g.getId());
					if (m != null)
						myString += "Minutes[" + " Winner: " + m.getWinner() + " Home Score: " + m.getHomeScore() + "Visitor Score: " + m.getVisitorScore() + "\r\n";
				}
				myString += "\r\n\r\n";

			}

			final Authority sp = new Authority();
			sp.setAuthority(Authority.SPONSOR);
			if (a.getUserAccount().getAuthorities().contains(sp)) {
				final Sponsor spo = this.sponsorService.findByUserAccount(a.getUserAccount());
				final Collection<Sponsorship> ss = this.sponsorshipService.findAllBySponsorId(spo.getId());

				myString += "Sponsorships:\r\n";
				for (final Sponsorship s : ss)
					myString += "Banner: " + s.getBanner() + " Target: " + s.getTarget() + " Credit card: " + s.getCreditCard() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority p = new Authority();
			p.setAuthority(Authority.PRESIDENT);
			if (a.getUserAccount().getAuthorities().contains(p)) {
				final President pr = this.presidentService.findByUserAccount(a.getUserAccount());
				final Team ts = this.teamService.findByPresidentId(pr.getId());
				final Collection<Signing> sss = this.signingService.findAllByPresident(pr.getId());
				final Collection<Hiring> hs = this.hiringService.findByPresidentDown(pr.getId());

				if (ts != null) {
					myString += "Team:\r\n";
					myString += "Name:" + ts.getName() + " Address: " + ts.getAddress() + " Badge URL: " + ts.getBadgeUrl() + " Stadium name: " + ts.getStadiumName() + " Establishment date: " + ts.getEstablishmentDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Signings:\r\n";
				for (final Signing s : sss)
					myString += "Offered clause: " + s.getOfferedClause() + " Comment: " + s.getMandatoryComment() + " Player name: " + s.getPlayer().getName() + " Price: " + s.getPrice() + " Status: " + s.getStatus() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Hiring managers\r\n";
				for (final Hiring h : hs)
					myString += " Comment: " + h.getMandatoryComment() + " Name of manager: " + h.getManager().getName() + " Price: " + h.getPrice() + " Status: " + h.getStatus() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority man = new Authority();
			man.setAuthority(Authority.MANAGER);
			if (a.getUserAccount().getAuthorities().contains(man)) {
				final Manager m = this.managerService.findByUserAccount(a.getUserAccount());
				final Collection<Hiring> hs = this.hiringService.findAllByManager(m.getId());
				final Collection<Training> ts = this.trainingService.findTrainingsByManagerId(m.getId());

				if (m.getTeam() != null)
					myString += "Team: " + m.getTeam().getName() + "\r\n\r\n";

				myString += "Hirings:\r\n";
				for (final Hiring h : hs)
					myString += "Comment: " + h.getMandatoryComment() + " Name of manager: " + h.getManager().getName() + " Price: " + h.getPrice() + " Status: " + h.getStatus() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Trainings:\r\n";
				for (final Training t : ts)
					myString += " Place: " + t.getPlace() + " Description: " + t.getDescription() + " Starts: " + t.getStartDate() + " Ends: " + t.getEndingDate() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority pla = new Authority();
			pla.setAuthority(Authority.PLAYER);
			if (a.getUserAccount().getAuthorities().contains(pla)) {
				final Player pl = this.playerService.findByUserAccount(a.getUserAccount());
				final StatisticalData std = this.statisticalDataService.findStatisticalDataByPlayerId(pl.getId());
				final History h = this.historyService.findByPlayerId(pl.getId());
				final Collection<Signing> sgs = this.signingService.findAllByPlayer(pl.getId());

				if (pl.getTeam() != null)
					myString += "Team: " + pl.getTeam().getName() + "\r\n\r\n";

				if (std != null) {
					myString += "Stats:\r\n";
					myString += "Yellow cards:" + std.getAccumulatedYellowCard() + " Red cards:" + std.getRedCards() + " Goals: " + std.getGoals() + " Matches played: " + std.getMatchsPlayed() + " Total red cards: " + std.getRedCards()
						+ " Total yellow cards: " + std.getYellowCards() + "\r\n";
				}

				if (h != null) {
					final Collection<PlayerRecord> prs = h.getPlayerRecords();
					myString += "Personal data:\r\n";
					myString += "Social profile:" + h.getPersonalData().getSocialNetworkProfilelink() + " Photos:" + h.getPersonalData().getPhotos() + "\r\n";

					myString += "Player Record:\r\n";
					for (final PlayerRecord pr : h.getPlayerRecords())
						myString += "\nSalary: " + pr.getSalary() + " Squad Number: " + pr.getSquadNumber() + " Starts: " + pr.getStartDate() + " Ends: " + pr.getEndDate() + "\r\n";
					myString += "Sports Records:\r\n";
					for (final SportRecord sr : h.getSportRecords())
						myString += " Sport name: " + sr.getSportName() + " Team sport: " + sr.getTeamSport() + " Starts: " + sr.getStartDate() + " Ends: " + sr.getEndDate() + "\r\n";
				}

				myString += "Signings:\r\n";
				for (final Signing s : sgs)
					myString += "Comment: " + s.getMandatoryComment() + " Clause: " + s.getOfferedClause() + " Price: " + s.getPrice() + " Status: " + s.getStatus() + "\r\n";

			}

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=my_data_as_system_user.txt");
			final ServletOutputStream out = response.getOutputStream();
			out.println(myString);
			out.flush();
			out.close();

		} else {
			String myString = "Debajo de estas lineas puedes encontrar todos los datos de usuario que tenemos de ti en Acme-Champions:\r\n";

			final Actor a = this.actorService.findByPrincipal();
			final Collection<Message> msgs = this.messageService.messagePerActor(a.getId());

			myString += "\r\n\r\n";

			myString += a.getUserAccount().getAuthorities();

			myString += "Nombre completo:" + a.getName() + " " + a.getSurnames() + "\r\n Dirrecion:" + a.getAddress() + "\r\n Email:" + a.getEmail() + "\r\n Telefono:" + a.getPhone() + "\r\n Foto:" + a.getPhoto() + " \r\n";
			myString += "\r\n\r\n";
			myString += "Mensajes:\r\n";
			for (final Message msg : msgs)
				myString += "Emisor: " + msg.getSender().getName() + " " + msg.getSender().getSurnames() + " Receptor: " + msg.getRecipient().getName() + " " + msg.getRecipient().getSurnames() + " Fecha de envio: " + msg.getMoment() + " Tema: "
					+ msg.getSubject() + " Cuerpo: " + msg.getBody() + " Etiquetas: " + msg.getTags() + "\r\n";
			myString += "\r\n\r\n";

			final Authority fede = new Authority();
			fede.setAuthority(Authority.FEDERATION);
			if (a.getUserAccount().getAuthorities().contains(fede)) {
				final Federation f = this.federationService.findByUserAccount(a.getUserAccount());
				final Collection<Competition> cs = this.competitionService.findByFederationId(f.getId());
				final Collection<Format> fs = this.formatService.findFormatByFederationId(f.getId());
				myString += "Competiciones:\r\n";
				for (final Competition c : cs)
					myString += "Nombre del trofeo:" + c.getNameTrophy() + " Empieza:" + c.getStartDate() + " Acaba:" + c.getEndDate() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Formatos:\r\n";
				for (final Format fo : fs)
					myString += "Tipo:" + fo.getType() + " Equipos maximos:" + fo.getMaximumTeams() + " Minimos equipos:" + fo.getMinimumTeams() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority r = new Authority();
			r.setAuthority(Authority.REFEREE);
			if (a.getUserAccount().getAuthorities().contains(r)) {
				final Referee re = this.refereeService.findByUserAccount(a.getUserAccount());
				final Collection<Game> gs = this.gameService.findGameByRefereeId(re.getId());

				myString += "Partidos:\r\n";
				for (final Game g : gs) {
					myString += "Lugar: " + g.getPlace() + " Empieza: " + g.getGameDate() + " Equipo Local: " + g.getHomeTeam().getName() + " Equipo Visitante: " + g.getVisitorTeam().getName() + "\r\n";
					final Minutes m = this.minutesService.findMinuteByGameId(g.getId());
					if (m != null)
						myString += "Acta[" + " Ganador: " + m.getWinner() + " Goles Local: " + m.getHomeScore() + "Goles Visitante: " + m.getVisitorScore() + "\r\n";
				}

				myString += "\r\n\r\n";
			}

			final Authority sp = new Authority();
			sp.setAuthority(Authority.SPONSOR);
			if (a.getUserAccount().getAuthorities().contains(sp)) {
				final Sponsor spo = this.sponsorService.findByUserAccount(a.getUserAccount());
				final Collection<Sponsorship> ss = this.sponsorshipService.findAllBySponsorId(spo.getId());

				myString += "Patrocinios:\r\n";
				for (final Sponsorship s : ss)
					myString += "Banner: " + s.getBanner() + " Objectivo: " + s.getTarget() + " Tarjeta de credito: " + s.getCreditCard() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority p = new Authority();
			p.setAuthority(Authority.PRESIDENT);
			if (a.getUserAccount().getAuthorities().contains(p)) {
				final President pr = this.presidentService.findByUserAccount(a.getUserAccount());
				final Team ts = this.teamService.findByPresidentId(pr.getId());
				final Collection<Signing> sss = this.signingService.findAllByPresident(pr.getId());
				final Collection<Hiring> hs = this.hiringService.findByPresidentDown(pr.getId());

				if (ts != null) {
					myString += "Equipo:\r\n";
					myString += "Nombre:" + ts.getName() + " Direccion: " + ts.getAddress() + " Foto escudo URL: " + ts.getBadgeUrl() + " Nombre del estadio: " + ts.getStadiumName() + " Fecha de fundacion: " + ts.getEstablishmentDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Ofertas:\r\n";
				for (final Signing s : sss)
					myString += "Clausula ofertada: " + s.getOfferedClause() + " Comentario: " + s.getMandatoryComment() + " Nombre del jugador: " + s.getPlayer().getName() + " Precio: " + s.getPrice() + " Estado: " + s.getStatus() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Contrato entrenadores:\r\n";
				for (final Hiring h : hs)
					myString += " Comentario: " + h.getMandatoryComment() + " Nombre del entrenador: " + h.getManager().getName() + " Precio: " + h.getPrice() + " Estado: " + h.getStatus() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority man = new Authority();
			man.setAuthority(Authority.MANAGER);
			if (a.getUserAccount().getAuthorities().contains(man)) {
				final Manager m = this.managerService.findByUserAccount(a.getUserAccount());
				final Collection<Hiring> hs = this.hiringService.findAllByManager(m.getId());
				final Collection<Training> ts = this.trainingService.findTrainingsByManagerId(m.getId());

				if (m.getTeam() != null)
					myString += "Equipo: " + m.getTeam().getName() + "\r\n\r\n";

				myString += "Contratos/Ofertas:\r\n";
				for (final Hiring h : hs)
					myString += " Comentario: " + h.getMandatoryComment() + " Nombre del entrenador: " + h.getManager().getName() + " Precio: " + h.getPrice() + " Estado: " + h.getStatus() + "\r\n";
				myString += "\r\n\r\n";
				myString += "Entrenamientos:\r\n";
				for (final Training t : ts)
					myString += " Lugar: " + t.getPlace() + " Descripcion: " + t.getDescription() + " Empieza: " + t.getStartDate() + " Acaba: " + t.getEndingDate() + "\r\n";

				myString += "\r\n\r\n";
			}

			final Authority pla = new Authority();
			pla.setAuthority(Authority.PLAYER);
			if (a.getUserAccount().getAuthorities().contains(pla)) {
				final Player pl = this.playerService.findByUserAccount(a.getUserAccount());
				final StatisticalData std = this.statisticalDataService.findStatisticalDataByPlayerId(pl.getId());
				final History h = this.historyService.findByPlayerId(pl.getId());
				final Collection<Signing> sgs = this.signingService.findAllByPlayer(pl.getId());

				if (pl.getTeam() != null)
					myString += "Equipo: " + pl.getTeam().getName() + "\r\n\r\n";

				if (std != null) {
					myString += "Estadisticas:\r\n";
					myString += "Tarjetas amarillas:" + std.getAccumulatedYellowCard() + " Tarjetas rojas:" + std.getRedCards() + " Goles: " + std.getGoals() + " Partidos jugados: " + std.getMatchsPlayed() + " Tarjetas rojas totales: " + std.getRedCards()
						+ " Tarjetas amarillas totales: " + std.getYellowCards() + "\r\n";
				}

				if (h != null) {
					final Collection<PlayerRecord> prs = h.getPlayerRecords();
					myString += "Datos personales:\r\n";
					myString += "Perfil Social:" + h.getPersonalData().getSocialNetworkProfilelink() + " Fotos:" + h.getPersonalData().getPhotos() + "\r\n";

					myString += "Historial del jugador:\r\n";
					for (final PlayerRecord pr : h.getPlayerRecords())
						myString += " Salario: " + pr.getSalary() + " Dorsal: " + pr.getSquadNumber() + " Empieza: " + pr.getStartDate() + " Acaba: " + pr.getEndDate() + "\r\n";
					myString += "Historial de deportes:\r\n";
					for (final SportRecord sr : h.getSportRecords())
						myString += " Deporte: " + sr.getSportName() + " Equipo: " + sr.getTeamSport() + " Empieza: " + sr.getStartDate() + " Acaba: " + sr.getEndDate() + "\r\n";
				}

				myString += "Contratos/Ofertas:\r\n";
				for (final Signing s : sgs)
					myString += " Comentario: " + s.getMandatoryComment() + " Clausula: " + s.getOfferedClause() + " Precio: " + s.getPrice() + " Estado: " + s.getStatus() + "\r\n";

			}

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=mis_datos_como_usuario_del_sistema.txt");
			final ServletOutputStream out = response.getOutputStream();
			out.println(myString);
			out.flush();
			out.close();

		}
	}
}
