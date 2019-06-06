
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
import security.Authority;

@Controller
@RequestMapping("/data")
public class DownloadDataActorController extends AbstractController {

	@Autowired
	private MessageService	messageService;

	@Autowired
	private ActorService	actorService;
	
	@Autowired
	private FederationService federationService;
	@Autowired
	private RefereeService refereeService;
	@Autowired
	private SponsorService sponsorService;
	@Autowired
	private PresidentService presidentService;
	@Autowired
	private ManagerService managerService;
	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private CompetitionService competitionService;
	@Autowired
	private FormatService formatService;
	@Autowired
	private GameService gameService;
	@Autowired
	private MinutesService minutesService;
	@Autowired
	private SponsorshipService sponsorshipService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private SigningService signingService;
	@Autowired
	private HiringService hiringService;
	@Autowired
	private TrainingService trainingService;
	@Autowired
	private FinderService finderService;
	@Autowired
	private StatisticalDataService statisticalDataService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private PersonalDataService personalDataService;
	@Autowired
	private PlayerRecordService playerRecordService;
	@Autowired
	private SportRecordService sportRecordService;


	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public void test(final HttpSession session, final HttpServletResponse response) throws IOException {

		final String language = LocaleContextHolder.getLocale().getLanguage();

		if (language == "en") {
			String myString = "Below these lines you can find all the user data we have at Acme-Champions:\r\n";

			final Actor a = this.actorService.findByPrincipal();
			final Collection<Message> msgs = this.messageService.messagePerActor(a.getId());

			myString += "\r\n\r\n";
			
			myString += a.getUserAccount().getAuthorities();

			myString += "Full name:" + a.getName() + " " +  a.getSurnames() + "\nAddress:" + a.getAddress() + "\nEmail:" + a.getEmail() + "\nPhone:" + a.getPhone() + "\nPhoto:" + a.getPhoto() + " \r\n";
			myString += "\r\n\r\n";
			myString += "Messages:\r\n";
			for (final Message msg : msgs)
				myString += "Sender: " + msg.getSender().getName() + " " + msg.getSender().getSurnames() + "\nRecipient: " + msg.getRecipient().getName() + " " + msg.getRecipient().getSurnames() + "\nMoment: " + msg.getMoment() + "\nSubject: "
					+ msg.getSubject() + "\nBody: " + msg.getBody() + "\nTags: " + msg.getTags();
			myString += "\r\n\r\n";
			
			final Authority fede = new Authority();
			fede.setAuthority(Authority.FEDERATION);
			if (a.getUserAccount().getAuthorities().contains(fede)) {
				final Federation f = this.federationService.findByUserAccount(a.getUserAccount());
				final Collection<Competition> cs = this.competitionService.findByFederationId(f.getId());
				final Collection<Format> fs = this.formatService.findFormatByFederationId(f.getId());
				myString += "Competitions:\r\n";
				for (final Competition c: cs) {
					myString += "Trophy name:" + c.getNameTrophy() + "\nStarts:" + c.getStartDate() + "\nEnds:" + c.getEndDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Formats:\r\n";
				for (final Format fo: fs) {
					myString += "Type:" + fo.getType() + "\nMaximum teams:" + fo.getMaximumTeams() + "\nMinimum teams:" + fo.getMinimumTeams() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority r = new Authority();
			r.setAuthority(Authority.REFEREE);
			if (a.getUserAccount().getAuthorities().contains(r)) {
				final Referee re = this.refereeService.findByUserAccount(a.getUserAccount());
				final Collection<Game> gs = this.gameService.findGameByRefereeId(re.getId());
				
				myString += "Games:\r\n";
				for (final Game g: gs) {
					myString += "Place: " + g.getPlace() + "\nStarts: " + g.getGameDate() + "\nLocal team: " + g.getHomeTeam() + "\nVisitor team: " + g.getVisitorTeam() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority sp = new Authority();
			sp.setAuthority(Authority.SPONSOR);
			if (a.getUserAccount().getAuthorities().contains(sp)) {
				final Sponsor spo = this.sponsorService.findByUserAccount(a.getUserAccount());
				final Collection<Sponsorship> ss = this.sponsorshipService.findAllBySponsorId(spo.getId());
				
				myString += "Sponsorships:\r\n";
				for (final Sponsorship s: ss) {
					myString += "Banner: " + s.getBanner() + "\nTarget: " + s.getTarget() + "\nCredit card: " + s.getCreditCard() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority p = new Authority();
			p.setAuthority(Authority.PRESIDENT);
			if (a.getUserAccount().getAuthorities().contains(p)) {
				final President pr = this.presidentService.findByUserAccount(a.getUserAccount());
				final Team ts = this.teamService.findByPresidentId(pr.getId());
				final Collection<Signing> sss = this.signingService.findAllByPresident(pr.getId());
				final Collection<Hiring> hs = this.hiringService.findByPresident(pr.getId());
				
				if (ts != null) {
					myString += "Team:\r\n";
					myString += "Name:" + ts.getName() + "\nAddress: " + ts.getAddress() + "\nBadge URL: " + ts.getBadgeUrl() + "\nStadium name: " + ts.getStadiumName() + "\nEstablishment date: " + ts.getEstablishmentDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Signings:\r\n";
				for (final Signing s: sss) {
					myString += "Offered clause: " + s.getOfferedClause() + "\nComment: " + s.getMandatoryComment()+ "\nPlayer name: " + s.getPlayer().getName() + "\nPrice: " + s.getPrice() + "\nStatus: " + s.getStatus() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Hiring managers\r\n";
				for (final Hiring h: hs) {
					myString += "\nComment: " + h.getMandatoryComment()+ "\nName of manager: " + h.getManager().getName() + "\nPrice: " + h.getPrice() + "\nStatus: " + h.getStatus() + "\r\n";
				}
				
			}
			
			myString += "\r\n\r\n";
			
			final Authority man = new Authority();
			man.setAuthority(Authority.MANAGER);
			if (a.getUserAccount().getAuthorities().contains(man)) {
				final Manager m = this.managerService.findByUserAccount(a.getUserAccount());
				final Collection<Hiring> hs = this.hiringService.findAllByManager(m.getId());
				final Collection<Training> ts  = this.trainingService.findTrainingsByManagerId(m.getId());
				
				myString += "Hirings:\r\n";
				for (final Hiring h: hs) {
					myString += "\nComment: " + h.getMandatoryComment()+ "\nName of manager: " + h.getManager().getName() + "\nPrice: " + h.getPrice() + "\nStatus: " + h.getStatus() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Trainings:\r\n";
				for (final Training t: ts) {
					myString += "\nPlace: " + t.getPlace() + "\nDescription: " + t.getDescription() + "\nStarts: " + t.getStartDate() + "\nEnds: " + t.getEndingDate() + "\r\n";
				}
				
			}
			
			myString += "\r\n\r\n";
			
			final Authority pla = new Authority();
			pla.setAuthority(Authority.PLAYER);
			if (a.getUserAccount().getAuthorities().contains(pla)) {
				final Player pl = this.playerService.findByUserAccount(a.getUserAccount());
				final StatisticalData std = this.statisticalDataService.findStatisticalDataByPlayerId(pl.getId());
				final History h = this.historyService.findByPlayerId(pl.getId());
				final Collection<Signing> sgs =  this.signingService.findAllByPlayer(pl.getId());
				
				
				if (std != null) {
					myString += "Stats:\r\n";
					myString += "Yellow cards:" + std.getAccumulatedYellowCard() + "\nRed cards:" + std.getRedCards() + "\nGoals: " + std.getGoals() + "\nMatches played: " + std.getMatchsPlayed() + "\nTotal red cards: " + std.getRedCards() + "\nTotal yellow cards: " + std.getYellowCards() + "\r\n";
				}
				
				if (h != null) {
					final Collection<PlayerRecord> prs = h.getPlayerRecords();
					myString += "Personal data:\r\n";
					myString += "Social profile:" + h.getPersonalData().getSocialNetworkProfilelink() + "\nPhotos:" + h.getPersonalData().getPhotos() + "\r\n";
					
					
					myString += "Player Record:\r\n";
					for (final PlayerRecord pr: h.getPlayerRecords()) {
						myString += "\nSalary: " + pr.getSalary() + "\nSquad Number: " + pr.getSquadNumber() + "\nStarts: " + pr.getStartDate() + "\nEnds: " + pr.getEndDate() + "\r\n";
					}
					myString += "Sports Records:\r\n";
					for (final SportRecord sr: h.getSportRecords()) {
						myString += "\nSport name: " + sr.getSportName() + "\nTeam sport: " + sr.getTeamSport() + "\nStarts: " + sr.getStartDate() + "\nEnds: " + sr.getEndDate() + "\r\n";
					}
				}
				
				
				myString += "Signings:\r\n";
				for (final Signing s: sgs) {
					myString += "\nComment: " + s.getMandatoryComment() +  "\nClause: " + s.getOfferedClause() + "\nPrice: " + s.getPrice() + "\nStatus: " + s.getStatus() + "\r\n";
				}

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

			myString += "Nombre completo:" + a.getName() + " " +  a.getSurnames() + "\nAddress:" + a.getAddress() + "\nEmail:" + a.getEmail() + "\nPhone:" + a.getPhone() + "\nPhoto:" + a.getPhoto() + " \r\n";
			myString += "\r\n\r\n";
			myString += "Mensajes:\r\n";
			for (final Message msg : msgs)
				myString += "Emisor: " + msg.getSender().getName() + " " + msg.getSender().getSurnames() + "\nReceptor: " + msg.getRecipient().getName() + " " + msg.getRecipient().getSurnames() + "\nFecha de envio: "
					+ msg.getMoment() + "\nTema: " + msg.getSubject() + "\nCuerpo: " + msg.getBody() + "\nEtiquetas: " + msg.getTags();
			myString += "\r\n\r\n";
			
			final Authority fede = new Authority();
			fede.setAuthority(Authority.FEDERATION);
			if (a.getUserAccount().getAuthorities().contains(fede)) {
				final Federation f = this.federationService.findByUserAccount(a.getUserAccount());
				final Collection<Competition> cs = this.competitionService.findByFederationId(f.getId());
				final Collection<Format> fs = this.formatService.findFormatByFederationId(f.getId());
				myString += "Competiciones:\r\n";
				for (final Competition c: cs) {
					myString += "Nombre del trofeo:" + c.getNameTrophy() + "\nEmpieza:" + c.getStartDate() + "\nAcaba:" + c.getEndDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Formatos:\r\n";
				for (final Format fo: fs) {
					myString += "Tipo:" + fo.getType() + "\nEquipos maximos:" + fo.getMaximumTeams() + "\nMinimos equipos:" + fo.getMinimumTeams() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority r = new Authority();
			r.setAuthority(Authority.REFEREE);
			if (a.getUserAccount().getAuthorities().contains(r)) {
				final Referee re = this.refereeService.findByUserAccount(a.getUserAccount());
				final Collection<Game> gs = this.gameService.findGameByRefereeId(re.getId());
				
				myString += "Partidos:\r\n";
				for (final Game g: gs) {
					myString += "Lugar: " + g.getPlace() + "\nEmpieza: " + g.getGameDate() + "\nLocal: " + g.getHomeTeam() + "\nVisitante: " + g.getVisitorTeam() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority sp = new Authority();
			sp.setAuthority(Authority.SPONSOR);
			if (a.getUserAccount().getAuthorities().contains(sp)) {
				final Sponsor spo = this.sponsorService.findByUserAccount(a.getUserAccount());
				final Collection<Sponsorship> ss = this.sponsorshipService.findAllBySponsorId(spo.getId());
				
				myString += "Patrocinios:\r\n";
				for (final Sponsorship s: ss) {
					myString += "Banner: " + s.getBanner() + "\nTarget: " + s.getTarget() + "\nTarjeta de credito: " + s.getCreditCard() + "\r\n";
				}
			}
			
			myString += "\r\n\r\n";
			
			final Authority p = new Authority();
			p.setAuthority(Authority.PRESIDENT);
			if (a.getUserAccount().getAuthorities().contains(p)) {
				final President pr = this.presidentService.findByUserAccount(a.getUserAccount());
				final Team ts = this.teamService.findByPresidentId(pr.getId());
				final Collection<Signing> sss = this.signingService.findAllByPresident(pr.getId());
				final Collection<Hiring> hs = this.hiringService.findByPresident(pr.getId());
				
				if (ts != null) {
					myString += "Equipo:\r\n";
					myString += "Nombre:" + ts.getName() + "\nDireccion: " + ts.getAddress() + "\nFoto escudo URL: " + ts.getBadgeUrl() + "\nNombre del estadio: " + ts.getStadiumName() + "\nFecha de fundacion: " + ts.getEstablishmentDate() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Ofertas:\r\n";
				for (final Signing s: sss) {
					myString += "Clausula ofertada: " + s.getOfferedClause() + "\nComentario: " + s.getMandatoryComment()+ "\nNombre del jugador: " + s.getPlayer().getName() + "\nPrecio: " + s.getPrice() + "\nEstado: " + s.getStatus() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Contrato entrenadores:\r\n";
				for (final Hiring h: hs) {
					myString += "\nComentario: " + h.getMandatoryComment()+ "\nNombre del entrenador: " + h.getManager().getName() + "\nPrecio: " + h.getPrice() + "\nEstado: " + h.getStatus() + "\r\n";
				}
				
			}
			
			myString += "\r\n\r\n";
			
			final Authority man = new Authority();
			man.setAuthority(Authority.MANAGER);
			if (a.getUserAccount().getAuthorities().contains(man)) {
				final Manager m = this.managerService.findByUserAccount(a.getUserAccount());
				final Collection<Hiring> hs = this.hiringService.findAllByManager(m.getId());
				final Collection<Training> ts  = this.trainingService.findTrainingsByManagerId(m.getId());
				
				myString += "Contratos/Ofertas:\r\n";
				for (final Hiring h: hs) {
					myString += "\nComentario: " + h.getMandatoryComment()+ "\nNombre del entrenador: " + h.getManager().getName() + "\nPrecio: " + h.getPrice() + "\nEstado: " + h.getStatus() + "\r\n";
				}
				myString += "\r\n\r\n";
				myString += "Entrenamientos:\r\n";
				for (final Training t: ts) {
					myString += "\nLugar: " + t.getPlace() + "\nDescripcion: " + t.getDescription() + "\nEmpieza: " + t.getStartDate() + "\nAcaba: " + t.getEndingDate() + "\r\n";
				}
				
			}
			
			myString += "\r\n\r\n";
			
			final Authority pla = new Authority();
			pla.setAuthority(Authority.PLAYER);
			if (a.getUserAccount().getAuthorities().contains(pla)) {
				final Player pl = this.playerService.findByUserAccount(a.getUserAccount());
				final StatisticalData std = this.statisticalDataService.findStatisticalDataByPlayerId(pl.getId());
				final History h = this.historyService.findByPlayerId(pl.getId());
				final Collection<Signing> sgs =  this.signingService.findAllByPlayer(pl.getId());
				
				
				if (std != null) {
					myString += "Estadisticas:\r\n";
					myString += "Tarjetas amarillas:" + std.getAccumulatedYellowCard() + "\nTarjetas rojas:" + std.getRedCards() + "\nGoles: " + std.getGoals() + "\nPartidos jugados: " + std.getMatchsPlayed() + "\nTarjetas rojas totales: " + std.getRedCards() + "\nTarjetas amarillas totales: " + std.getYellowCards() + "\r\n";
				}
				
				if (h != null) {
					final Collection<PlayerRecord> prs = h.getPlayerRecords();
					myString += "Datos personales:\r\n";
					myString += "Perfil Social:" + h.getPersonalData().getSocialNetworkProfilelink() + "\nFotos:" + h.getPersonalData().getPhotos() + "\r\n";
					
					
					myString += "Historial del jugador:\r\n";
					for (final PlayerRecord pr: h.getPlayerRecords()) {
						myString += "\nSalario: " + pr.getSalary() + "\nDorsal: " + pr.getSquadNumber() + "\nEmpieza: " + pr.getStartDate() + "\nAcaba: " + pr.getEndDate() + "\r\n";
					}
					myString += "Historial de deportes:\r\n";
					for (final SportRecord sr: h.getSportRecords()) {
						myString += "\nDeporte: " + sr.getSportName() + "\nEquipo: " + sr.getTeamSport() + "\nEmpieza: " + sr.getStartDate() + "\nAcaba: " + sr.getEndDate() + "\r\n";
					}
				}
				
				
				myString += "Contratos/Ofertas:\r\n";
				for (final Signing s: sgs) {
					myString += "\nComentario: " + s.getMandatoryComment() +  "\nClausula: " + s.getOfferedClause() + "\nPrecio: " + s.getPrice() + "\nEstado: " + s.getStatus() + "\r\n";
				}

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
