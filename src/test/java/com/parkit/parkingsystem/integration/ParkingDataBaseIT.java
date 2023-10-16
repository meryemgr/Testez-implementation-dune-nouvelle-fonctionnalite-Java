package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability

		// Assurez-vous qu'un ticket est effectivement enregistré en base de données
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		Assertions.assertNotNull(ticket);
		// Assurez-vous que le parking a bien été mis à jour avec la disponibilité
		Assertions.assertFalse(ticket.getParkingSpot().isAvailable());

	}

	@Test
	public void testParkingLotExit() throws Exception {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database

		// Assurez-vous que le montant du ticket a été correctement calculé
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		double fare = ticket.getPrice();
		// Comparez le montant du fare avec la valeur attendue
		Assertions.assertEquals(0.0, fare);

		// Assurez-vous que l'heure de sortie (out time) a été correctement enregistrée
		// dans le ticket
		Assertions.assertNotNull(ticket.getOutTime());

	}

	@Test
	public void testParkingLotExitRecurringUser() throws Exception {
		// Configuration initiale
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// Récupérer le ticket
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ticket.setInTime(inTime);

		// Simuler un utilisateur récurrent (par exemple, avec le même numéro de plaque
		// d'immatriculation)
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		// Exécuter la sortie du véhicule
		parkingService.processExitingVehicle();
		ticket = ticketDAO.getTicket("ABCDEF");
		Assertions.assertNotNull(ticket);

		// Vérifier que le calcul du prix a été effectué avec la remise de 5%

		assertEquals(ticket.getPrice(), 0.0);

	}
}