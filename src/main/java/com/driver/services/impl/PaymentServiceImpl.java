package com.driver.services.impl;

import com.driver.Exception.NotFoundException;
import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Optional<Reservation> optionalReservation=reservationRepository2.findById(reservationId);
        if(!optionalReservation.isPresent())
        {
            throw new NotFoundException("Reservation not found");
        }
        Reservation reservation=optionalReservation.get();

        double billAmount=calculateBillAmount(reservation);

        if(amountSent<billAmount)
            throw new NotFoundException("Insufficient Amount");

        PaymentMode paymentMode=validatePaymentMode(mode);

        Payment payment=new Payment();
        payment.setReservation(reservation);
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);

        reservation.setPayment(payment);
        reservationRepository2.save(reservation);
        paymentRepository2.save(payment);
        return payment;
    }

    private PaymentMode validatePaymentMode(String mode) {
        String upperCaseMode = mode.toUpperCase();
        if(upperCaseMode.equals("CASH")|| upperCaseMode.equals("CARD") || upperCaseMode.equals("UPI"))
        {
            return PaymentMode.valueOf(upperCaseMode);
        }
        else
        {
            throw new NotFoundException("Payment mode not detected");
        }
    }

    private double calculateBillAmount(Reservation reservation) {

        return reservation.getSpot().getPricePerHour()*reservation.getNumberOfHours();
    }
}
