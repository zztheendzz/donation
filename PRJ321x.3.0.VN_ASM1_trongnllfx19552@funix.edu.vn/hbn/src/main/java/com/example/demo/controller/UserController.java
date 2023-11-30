package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dao.DateTime;
import com.example.demo.dao.DonationDao;
import com.example.demo.dao.LoginDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.UserDonationDao;
import com.example.demo.model.Donation;
import com.example.demo.model.User;
import com.example.demo.model.UserDonation;

import DTO.UserDonationDTO;

@Controller
@RequestMapping("/public")
public class UserController {
	@Autowired
	@Qualifier("DonationDao")
	private DonationDao dd;
	
	@Autowired
	@Qualifier("UserDao")
	private UserDao ud;
	
	@Autowired
	@Qualifier("UserDonationDao")
	private UserDonationDao udd;
	

	private LoginDao loginD = new LoginDao();
	private User u = new User();
	DateTime dt = new DateTime();
	
	
	
	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

//	đăng nhập
	@GetMapping("/login")
	public String login() {
		return"admin/login";
	}
	
//	xác định user 
	@PostMapping("/login")
	public String loginPost(@ModelAttribute("user") User user, Model model) {
		User us = new User();
		boolean error = true;
		int check = loginD.check(user.getUserName(), user.getPassword(), ud.getListUser());
//		nếu =1 là admin
		if (check == 1) {
			us= loginD.user(user.getUserName(), user.getPassword(), ud.getListUser());
			us.setCreated(dt.getDate());
			ud.update(us);
			setU(loginD.getU());
			return "redirect:/ql-user/account";
		} else if (check == 2) {
//			 xử lý nếu là user =2
			us= loginD.user(user.getUserName(), user.getPassword(), ud.getListUser());
			us.setCreated(dt.getDate());
			ud.update(us);
			setU(us);
			model.addAttribute("user", us);
			return "redirect:/public/home";
		} else {
			error = true;
			model.addAttribute("error", error);
		}
//		 xử lý nếu k đúng đăng nhập
		return "admin/login";
		}
	
//  hàm xử lý khi đăng nhập là user
	@GetMapping("/home")
	public String home(Model model) {
		System.out.println(this.u.getId());
		List<Donation> list= dd.getDonationList();
		model.addAttribute("listdonation", list);
		model.addAttribute("user", getU());
		return "public/home";
	}
//	hàm xử lý khi ấn vào 1 donation cụ thể, 1 : donation.status =1
	@GetMapping("/detail/{donationId}")
	public String detail(@PathVariable("donationId") int donationId, Model model) {
		List<UserDonation> userDonationList= udd.GetListByIdD(donationId,1);
		model.addAttribute("userDonationList", userDonationList);
		model.addAttribute("user", getU());
		model.addAttribute("donationId", donationId);
		
		Donation donation = dd.getDonationById(donationId);
		model.addAttribute("donation", donation);

		return "public/detail";
	}
// thêm donatiton
	@PostMapping("/addDonation")
	public String addD( @ModelAttribute("UserDonationDTO") UserDonationDTO userDonationDTO ) {
		UserDonation UD = new UserDonation(userDonationDTO);
		UD.setCreated(dt.getDate());
		UD.setUser(ud.getUserById(userDonationDTO.getUserId()));
		UD.setDonation(dd.getDonationById(userDonationDTO.getDonationId()));
		
		udd.save(UD);
		return"redirect:/public/home";
	}
//	Success
	public void success() {
		
	}

}
