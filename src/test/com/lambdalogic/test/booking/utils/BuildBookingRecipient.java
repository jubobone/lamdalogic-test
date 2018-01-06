package com.lambdalogic.test.booking.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.lambdalogic.test.booking.model.Booking;
import com.lambdalogic.test.booking.model.Price;
import com.lambdalogic.test.booking.util.TypeHelper;

/**
 * Super class for generating random value with use solely for testing purpose.
 * 
 */

enum CurrencyISO { EUR, THB};

public class BuildBookingRecipient {

	private final int DEFAULT_TAXRATE = 10;
	private String defaultCurrency = CurrencyISO.EUR.toString();
	private final int NUMBER_CURRENCY = 2;
	
	public List<Booking> buildBookingListRandomly(int numberRecipient, 
			boolean isCurrencyChange, boolean isGross)  throws ParseException {
		List<Booking> results = new ArrayList<>();
		BigDecimal taxRate = TypeHelper.toBigDecimal(DEFAULT_TAXRATE);
		LocalDate localDate = LocalDate.now();
		
		for(int bookingId = 0; bookingId < numberRecipient; bookingId++){
			
			String currency = (isCurrencyChange) 
					? CurrencyISO.values()[bookingId % NUMBER_CURRENCY].toString()
					:defaultCurrency;
			
			Price mainPrice = generatePriceRamdomly(generateRandomNumber(2000,3000), 
					currency, taxRate, isGross);
			Price addPrice1 = generatePriceRamdomly(generateRandomNumber(1000,2000), 
					currency, taxRate, isGross);
			Price addPrice2 = generatePriceRamdomly(generateRandomNumber(500,1000), 
					currency, taxRate, isGross);
			Price cancelFee = generatePriceRamdomly(generateRandomNumber(0,500), 
					currency, taxRate, isGross);
			BigDecimal paidAmount = generateRandomNumber(200,500);
			Date bookingDate = 
					Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date cancelationDate = 
					Date.from(localDate.plusDays(1)
							.atStartOfDay(ZoneId.systemDefault()).toInstant());
			List<Long> recipientIds = getFixedRecipientList(numberRecipient);
			long invoiceRecipientIdPK = bookingId % 3; // generate 3 unique number which more than number of testing currency
			long offeringPK = bookingId + 10; // just randomly generate this id
			Booking booking = new Booking(
					(long)bookingId + 1,
					mainPrice,
					addPrice1,
					addPrice2,
					cancelFee,
					paidAmount,
					bookingDate,
					cancelationDate,
					recipientIds,
					invoiceRecipientIdPK,
					offeringPK
					);
			results.add(booking);
		}
			
		
		return results;
	}
	
	private List<Long> getFixedRecipientList(int numberRecipient) {
		List<Long> results = new ArrayList<Long>();
		
		for(int i = 0; i < numberRecipient; i++) {
			results.add((long)i + 1);
		}
		
		return results;
	}
	
	private Price generatePriceRamdomly(BigDecimal amount,
			String currency, BigDecimal taxRate, boolean isGross){
		return new Price(
				amount,
				currency, 
				taxRate, 
				isGross);
	}
	
	private BigDecimal generateRandomNumber(int rangeMin, int rangeMax) throws ParseException {
		Random r = new Random();
		double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		randomValue = Math.round(randomValue);
		return TypeHelper.toBigDecimal(randomValue).setScale(2);
	}
}
