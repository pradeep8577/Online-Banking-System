package com.bank.controller;

import java.time.LocalDate;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bank.dao.AccountDao;
import com.bank.dao.CheckbookDao;
import com.bank.model.Account;
import com.bank.model.Checkbook;
import com.bank.model.Customer;
import com.bank.utility.Constants.CheckbookStatus;

import jakarta.servlet.http.HttpSession;

@Controller
public class AccountController 
{

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private CheckbookDao checkbookDao;
	
	@PostMapping("/addaccount")
	public ModelAndView registerAdmin(@ModelAttribute Account account) {
		ModelAndView mv = new ModelAndView();
		
		accountDao.save(account);
		
		mv.addObject("status", "Account Added Successfully!");
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("/changepin")
	public String goToCahnegPinPage() {
		return "changepin";
	}
	
	@PostMapping("/changepin")
	public ModelAndView changePin(@RequestParam("currentpin") int currentPin, @RequestParam("newpin") int newPin, HttpSession session) {
		ModelAndView mv = new ModelAndView();
		
		Customer customer = (Customer)session.getAttribute("active-user");
		Account account = null;
		Optional<Account> o = accountDao.findById(customer.getId());
		if(o.isPresent()) {
			account = o.get();
		}
		
		account.setPin(newPin);
		accountDao.save(account);
		
		mv.addObject("status", "Pin changed Successfully!");
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("/changeAccountStatus")
	public ModelAndView changeAccountStatus(@RequestParam("accountId") int accountId, @RequestParam("accountStatus") String accountStatus) {
		ModelAndView mv = new ModelAndView();
		
		Account account = accountDao.findById(accountId).get();
		account.setStatus(accountStatus);
		
		accountDao.save(account);
		
		mv.addObject("status", "Account Status Updated!");
		mv.setViewName("index");
		return mv;
	}

	@GetMapping("/requestCheckbook")
	public ModelAndView requestCheckbook(@RequestParam("customerId") int customerId, HttpSession session) {
		ModelAndView mv = new ModelAndView();
	
		Checkbook checkbook = new Checkbook();
		checkbook.setCustomerId(customerId);
		checkbook.setAppliedDate(LocalDate.now().toString());
		checkbook.setApprovalStatus(CheckbookStatus.APPROVAL_PENDING.value());
		checkbookDao.save(checkbook);
		
		mv.addObject("status", "Successfully Requested for Chequebook!");
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("/updateCheckbookStatus")
	public ModelAndView updateCheckbookStatus(@RequestParam("customerId") int customerId, @RequestParam("status") String status) {
		ModelAndView mv = new ModelAndView();
		
		Checkbook checkbook = new Checkbook();
		checkbook = checkbookDao.findByCustomerId(customerId);
		checkbook.setApprovalStatus(status);
		checkbookDao.save(checkbook);
	
		mv.addObject("status", "Chequebook Approval Status Updated!");
		mv.setViewName("index");
		return mv;
	}
}
