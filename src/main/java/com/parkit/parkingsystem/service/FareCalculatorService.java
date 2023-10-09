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

		// TODO: Some tests are failing here. Need to check if this logic is correct
		// On obtient le temps passé dans le parking en Millis
		double duration = outHour - inHour;

		// On divise le resultat par 3 600 000 qui equivaut à une heure en Millis afin
		// de le convertir sur une base 1.
		double difference = duration / 3600000;
		System.out.println(ticket.getInTime());
		System.out.println(ticket.getOutTime());
		System.out.println(inHour / 3600000);
		System.out.println(outHour / 3600000);
		System.out.println(difference);
		if (difference <= 0.5) {

			ticket.setPrice(0.0);

		} else {

			switch (ticket.getParkingSpot().getParkingType()) {

			case CAR: {
				// Ajout d'une conditions a fin d'ajouter 5% de reduction au utilisateur
				// regulier.
				ticket.setPrice((isRecurent == false) ? (difference - 0.5) * Fare.CAR_RATE_PER_HOUR
						: (difference - 0.5) * Fare.CAR_RATE_PER_HOUR * 0.95);
				break;
				// ticket.setPrice((difference - 0.5) * Fare.CAR_RATE_PER_HOUR);
			}

			case BIKE: {
				// Ajout d'une condtions afin d'ajouter 5% de reduction au utilisateur regulier.
				ticket.setPrice((isRecurent == false) ? (difference - 0.5) * Fare.BIKE_RATE_PER_HOUR
						: (difference - 0.5) * Fare.BIKE_RATE_PER_HOUR * 0.95);

				// ticket.setPrice((difference - 0.5) * Fare.BIKE_RATE_PER_HOUR);
				break;
			}

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}
}