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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.DateTime;
import com.example.demo.dao.DonationDao;
import com.example.demo.dao.RoleDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dao.UserDonationDao;
import com.example.demo.model.Donation;
import com.example.demo.model.User;
import com.example.demo.model.UserDonation;

import DTO.DonationDTO;
import DTO.UserDTO;

@Controller
@RequestMapping("/ql-user")
public class AdminController {
	@Autowired
	@Qualifier("UserDao")
	private UserDao ud;
	
	@Autowired
	@Qualifier("RoleDao")
	private RoleDao rd;
	
	@Autowired
	@Qualifier("DonationDao")
	private DonationDao dd;
	
	@Autowired
	@Qualifier("UserDonationDao")
	private UserDonationDao udd;
	
	DateTime dt = new DateTime();
	
	
//	 khi đăng nhập la admin
	@GetMapping("/account")
	public String acconut(Model model) {
		List<User> list = ud.getListUser();
		model.addAttribute("list",list);
		return "admin/account";
	}
//	thêm mới user
	@PostMapping("/account")
	public String addUser(@ModelAttribute("userDTO") UserDTO userDTO) {
		
		userDTO.setCreatedAt(dt.getDate());
		ud.saveUser(userDTO);
		return "redirect:/ql-user/account";
	}
//	xoá user
	@PostMapping("/delete")
	public String deleteUser(@RequestParam("idUser") int idUser) {
		ud.delete(idUser);
		return "redirect:/ql-user/account";
	}
//	update user
	@PostMapping("/update")
	public String updateUser(@ModelAttribute("userDTOUpdate") UserDTO userDTOUpdate) {
		User user = new User(userDTOUpdate);
		ud.update(user);
		return "redirect:/ql-user/account";
	}
//	khoá/ mở khoá user
	@PostMapping("/lock")
	public String lock (@RequestParam("idUser") int idUser) {
		ud.lock_UnlockUser(idUser);
		return "redirect:/ql-user/account";
	}
	
//	Donation
	@GetMapping("/donation")
	public String donation(Model model) {
		List<Donation> list = dd.getDonationList();
		model.addAttribute("list",list);
		return "admin/donation";
	}
//	them donation
	@PostMapping("/donation")
	public String addDonation(@ModelAttribute("donationDTO") DonationDTO donationDTO) {
		dd.saveDonation(donationDTO);
		return "redirect:/ql-user/donation";
	}
//	xoá donation
	@PostMapping("/deleteDonation")
	public String deleteDonation(@RequestParam("idDonation") int idDonation) {
		dd.delete(idDonation);
		return "redirect:/ql-user/donation";
	}

// update donation	
	@PostMapping("/updateDonation")
	public String updateDonation(@ModelAttribute("donationDTOUpdate") DonationDTO donationDTOUpdate) {
		Donation donation = new Donation(donationDTOUpdate);
		dd.update(donation);
		return "redirect:/ql-user/donation";
	}
	
	// detail donation	
		@GetMapping("/detail/{donationId}")
		public String DetailDonation(@PathVariable("donationId") int donationId, Model model) {
//			3: lấy hết danh sách userdonation
			List<UserDonation> userDonationList = udd.GetListByIdD(donationId,3);
			Donation donation = dd.getDonationById(donationId);
			model.addAttribute("userDonationList", userDonationList);
			model.addAttribute("donation", donation);

			return "admin/detail";
		}
//		 lock donation
		@PostMapping("/lockDonation")
		public String lockDonation(@RequestParam("idD") int idD) {
			dd.lockDonation(idD);
			return "redirect:/ql-user/donation";
		}
//		xac nhan/huy xac nhan
		@PostMapping("/validations")
		public String validations(@RequestParam("idUserD") int idUserD) {
			
			UserDonation ud = udd.GetUserDById(idUserD);
			if(ud.getStatus() ==0 ) {
				ud.setStatus(1);
			}else {ud.setStatus(0);}
			udd.update(ud);
			return "redirect:/ql-user/donation";
		}
}
