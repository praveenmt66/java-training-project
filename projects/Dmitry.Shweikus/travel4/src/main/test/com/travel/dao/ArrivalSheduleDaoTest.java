/**
 * 
 */
package com.travel.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.travel.enums.TransportKind;
import com.travel.enums.TravelKind;
import com.travel.exceptions.DbSqlException;
import com.travel.pojo.TourShedule;
import com.travel.pojo.Tour;

/**
 * @author dima
 *
 */
public class ArrivalSheduleDaoTest
{
	TourSheduleDao dao = new TourSheduleDao();
	TourDao tourDao = new TourDao();

	@Before
	@After
	public void setUp() throws Exception
	{
		TestDatabaseDeleter.clearDatabase();
	}

	/**
	 * Test method for {@link com.travel.dao.BaseDao#update(com.travel.pojo.BaseEntity)}.
	 * @throws DbSqlException 
	 */
	@Test
	public void testUpdate() throws DbSqlException
	{
		Tour tour = tourDao.create("-----", TransportKind.TRAIN, TravelKind.REST, "---", "---", 1);
		@SuppressWarnings("deprecation")
		Date date = new Date(2012-1900,11,6);
		TourShedule shedule1 = dao.create(tour, date, 700.51, 20);
		TourShedule shedule2 = dao.create(tour, date, 800.51, 20);
		TourShedule shedule1a = dao.findById(shedule1.getId());
		
		assertNotNull(shedule1);
		assertNotNull(shedule2);
		assertEquals(shedule1.getId(), shedule1a.getId());
		
		@SuppressWarnings("deprecation")
		Date date2 = new Date(2012-1900,11,9); 
		
		shedule1.setDate(date2);
		dao.update(shedule1);
	}

}
