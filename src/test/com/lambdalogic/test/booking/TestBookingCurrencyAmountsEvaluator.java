package com.lambdalogic.test.booking;

import java.text.ParseException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.lambdalogic.test.booking.BookingsCurrencyAmountsEvaluator;
import com.lambdalogic.test.booking.IBookingsCurrencyAmountsEvaluator;
import com.lambdalogic.test.booking.exception.InconsistentCurrenciesException;
import com.lambdalogic.test.booking.model.Booking;
import com.lambdalogic.test.booking.model.CurrencyAmount;
import com.lambdalogic.test.booking.util.TypeHelper;
import com.lambdalogic.test.booking.utils.BuildBookingRecipient;

public class TestBookingCurrencyAmountsEvaluator extends BuildBookingRecipient {

	private IBookingsCurrencyAmountsEvaluator bookingCurrency;
	private List<Booking> fixtures;
	Booking booking;
	
	@Before
	public void setup() throws ParseException {
		bookingCurrency = new BookingsCurrencyAmountsEvaluator();
		fixtures = buildBookingListRandomly(4, false, true);
	}
	
	@Test
	public void testBuildBookingsListNotNull() {
		Assert.assertNotNull(fixtures);
	}
	
	@Test
	public void testBookingsCurrencyAmountsEvaluatorCalculation() throws InconsistentCurrenciesException {
		bookingCurrency.calculate(fixtures, 001l);
		Assert.assertNotNull(bookingCurrency.getTotalAmount());
		Assert.assertNotNull(bookingCurrency.getTotalPaidAmount());
		Assert.assertNotNull(bookingCurrency.getTotalOpenAmount());
	}
	
	@Test(expected = InconsistentCurrenciesException.class)
	public void testMisMatchedBookingsCurrency() throws InconsistentCurrenciesException, ParseException {
		fixtures = buildBookingListRandomly(5, true, false);
		bookingCurrency.calculate(fixtures, 001l);
		Assert.assertNull(bookingCurrency.getTotalAmount());
		Assert.assertNull(bookingCurrency.getTotalPaidAmount());
		Assert.assertNull(bookingCurrency.getTotalOpenAmount());
	}
	
	@Test
	public void testMixUpGrossAndNetAmount() throws InconsistentCurrenciesException, ParseException {
		fixtures = buildBookingListRandomly(5, false, false);
		bookingCurrency.calculate(fixtures, 001l);
		Assert.assertEquals(new CurrencyAmount(TypeHelper.toBigDecimal(0), "EUR"), bookingCurrency.getTotalAmount());
		Assert.assertNotNull(bookingCurrency.getTotalPaidAmount());
		Assert.assertNotNull(bookingCurrency.getTotalOpenAmount());
	}
    
	@After
	public void tearDown() {
		fixtures = null;
	}
}
