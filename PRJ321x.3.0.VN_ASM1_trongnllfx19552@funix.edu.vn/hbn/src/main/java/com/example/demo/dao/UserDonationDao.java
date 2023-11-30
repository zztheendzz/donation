package com.example.demo.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.UserDonation;

@Repository
@Transactional
@Component("UserDonationDao")
public class UserDonationDao {

	@Autowired
	private SessionFactory factory;

	@Qualifier("DonationDao")
	private DonationDao dd;

	@Autowired
	@Qualifier("UserDao")
	private UserDao ud;

	public Session getSession() {
		Session session = factory.getCurrentSession();
		if (session == null) {
			session = factory.openSession();
		}

		return session;
	}
// lưu 
	public void save(UserDonation userDonation) {
		try {
			getSession().save(userDonation);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	public List<UserDonation> getUserDonationlist() {
		List<UserDonation> listUserDonation = null;
//		from User => User là tên class, truy vấn theo tên lớp chứ k còn theo tên bảng
		listUserDonation = getSession().createQuery("from UserDonation", UserDonation.class).getResultList();
		return listUserDonation;
	}
//	get total money

	public int Money(int idDonation) {
		int total = 0;
		List<UserDonation> listUserDonation = GetListByIdD(idDonation, 1);
		for (UserDonation ud : listUserDonation) {
			total = total + ud.getMoney();
		}
		return total;
	}
// lấy ra list userDonation tương ứng vs các trạng thái 

	public List<UserDonation> GetListByIdD(int idD, int status) {
			if (status == 3) {
				String hql = " from UserDonation  where donationId = :idD";
				Query query = getSession().createQuery(hql);
				query.setParameter("idD", idD);
				List<UserDonation> listUserDonation = query.getResultList();
				return listUserDonation;
			} else {
				String hql = "from UserDonation where donationId = :idD  and status = :status";
				Query query = getSession().createQuery(hql);
				query.setParameter("idD", idD);
				query.setParameter("status", status);
				List<UserDonation> listUserDonation = query.getResultList();
				return listUserDonation;
			}
		}
//	lấy ra UD theo id
	public UserDonation GetUserDById(int id) {
		UserDonation uD = new UserDonation();
		
		for(UserDonation ud: getUserDonationlist()) {
			if(ud.getId() == id) {
				uD= ud;
			}
		}
			return uD;
		}
	
	public List<UserDonation> GetUserDList() {
		List<UserDonation> listUserDonation = getSession().createQuery("from UserDonation",UserDonation.class).getResultList();
			return listUserDonation;
		}
//	update UD
	public void update(UserDonation uD) {
		List<UserDonation>getListUserD = GetUserDList();
		
		try{
			for(UserDonation ud : getListUserD ) {
				if(uD.getId()==ud.getId()) {
				getSession().evict(ud);
				ud = uD;
				getSession().update(ud);
				break;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		

		}
	}

