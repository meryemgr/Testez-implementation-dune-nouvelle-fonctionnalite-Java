package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean isRecurent) {

		if ((ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		// On obtient le temps passé dans le parking en Millis
		double duration = outHour - inHour;

		// On divise le resultat par 3 600 000 qui equivaut à une heure en Millis afin
		// de le convertir sur une base 1.
		double difference = duration / 3600000;

		if (difference <= 0.5) {

			ticket.setPrice(0.0);

		} else {

			switch (ticket.getParkingSpot().getParkingType()) {

			case CAR: {
				// Ajout d'une conditions a fin d'ajouter 5% de reduction au utilisateur
				// regulier.
				ticket.setPrice((isRecurent == false) ? difference * Fare.CAR_RATE_PER_HOUR
						: difference * Fare.CAR_RATE_PER_HOUR * 0.95);
				break;

			}

			case BIKE: {
				// Ajout d'une condtions afin d'ajouter 5% de reduction au utilisateur regulier.
				ticket.setPrice((isRecurent == false) ? difference * Fare.BIKE_RATE_PER_HOUR
						: difference * Fare.BIKE_RATE_PER_HOUR * 0.95);

				break;
			}

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}
}