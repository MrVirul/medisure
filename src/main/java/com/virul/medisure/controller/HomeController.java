package com.virul.medisure.controller;

import com.virul.medisure.model.Policy;
import com.virul.medisure.repository.ClaimRepository;
import com.virul.medisure.repository.PolicyRepository;
import com.virul.medisure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for public landing page and statistics
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    
    /**
     * Root path - show landing page or redirect to dashboard if authenticated
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        
        // Load statistics for the landing page
        Map<String, Object> stats = getStatistics();
        model.addAttribute("stats", stats);
        
        // Load active policies
        List<Policy> policies = policyRepository.findByIsActive(true);
        model.addAttribute("policies", policies);
        
        return "index";
    }
    
    /**
     * Home path - same as root
     */
    @GetMapping("/home")
    public String homeAlt(Authentication authentication, Model model) {
        return home(authentication, model);
    }
    
    /**
     * Index path - same as root
     */
    @GetMapping("/index")
    public String index(Authentication authentication, Model model) {
        return home(authentication, model);
    }
    
    /**
     * API endpoint for real-time statistics (HTMX polling)
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public String getStatsFragment() {
        Map<String, Object> stats = getStatistics();
        
        return String.format("""
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 xl:gap-10 max-w-6xl mx-auto">
                <!-- Registered Users Card -->
                <div class="bg-white bg-opacity-10 backdrop-blur-sm rounded-2xl p-8 lg:p-10 xl:p-12 border border-white border-opacity-20 hover:bg-opacity-15 hover:scale-105 transition-all duration-300 min-h-[280px] flex items-center justify-center" 
                     x-data="{ count: 0, target: %d }" 
                     x-init="setTimeout(() => { let interval = setInterval(() => { if(count < target) { count += Math.ceil(target/50); if(count > target) count = target; } else { clearInterval(interval); } }, 20); }, 100)">
                    <div class="flex flex-col items-center text-center w-full">
                        <div class="w-20 h-20 lg:w-24 lg:h-24 bg-gradient-to-br from-blue-400 to-blue-500 rounded-2xl flex items-center justify-center mb-6 shadow-lg">
                            <i data-lucide="users" class="w-10 h-10 lg:w-12 lg:h-12 text-white"></i>
                        </div>
                        <div class="text-6xl lg:text-7xl font-extrabold text-white mb-3 tracking-tight" x-text="count.toLocaleString() + '+'">%d+</div>
                        <div class="text-lg lg:text-xl text-blue-100 font-semibold leading-tight whitespace-nowrap">Registered<br class="sm:hidden">Users</div>
                    </div>
                </div>
                
                <!-- Active Policy Holders Card -->
                <div class="bg-white bg-opacity-10 backdrop-blur-sm rounded-2xl p-8 lg:p-10 xl:p-12 border border-white border-opacity-20 hover:bg-opacity-15 hover:scale-105 transition-all duration-300 min-h-[280px] flex items-center justify-center" 
                     x-data="{ count: 0, target: %d }" 
                     x-init="setTimeout(() => { let interval = setInterval(() => { if(count < target) { count += Math.ceil(target/50); if(count > target) count = target; } else { clearInterval(interval); } }, 20); }, 200)">
                    <div class="flex flex-col items-center text-center w-full">
                        <div class="w-20 h-20 lg:w-24 lg:h-24 bg-gradient-to-br from-green-400 to-green-500 rounded-2xl flex items-center justify-center mb-6 shadow-lg">
                            <i data-lucide="shield-check" class="w-10 h-10 lg:w-12 lg:h-12 text-white"></i>
                        </div>
                        <div class="text-6xl lg:text-7xl font-extrabold text-white mb-3 tracking-tight" x-text="count.toLocaleString() + '+'">%d+</div>
                        <div class="text-lg lg:text-xl text-blue-100 font-semibold leading-tight whitespace-nowrap">Active<br class="sm:hidden lg:inline"> Policy<br>Holders</div>
                    </div>
                </div>
                
                <!-- Policies Issued Card -->
                <div class="bg-white bg-opacity-10 backdrop-blur-sm rounded-2xl p-8 lg:p-10 xl:p-12 border border-white border-opacity-20 hover:bg-opacity-15 hover:scale-105 transition-all duration-300 min-h-[280px] flex items-center justify-center" 
                     x-data="{ count: 0, target: %d }" 
                     x-init="setTimeout(() => { let interval = setInterval(() => { if(count < target) { count += Math.ceil(target/50); if(count > target) count = target; } else { clearInterval(interval); } }, 20); }, 300)">
                    <div class="flex flex-col items-center text-center w-full">
                        <div class="w-20 h-20 lg:w-24 lg:h-24 bg-gradient-to-br from-orange-400 to-orange-500 rounded-2xl flex items-center justify-center mb-6 shadow-lg">
                            <i data-lucide="file-text" class="w-10 h-10 lg:w-12 lg:h-12 text-white"></i>
                        </div>
                        <div class="text-6xl lg:text-7xl font-extrabold text-white mb-3 tracking-tight" x-text="count.toLocaleString() + '+'">%d+</div>
                        <div class="text-lg lg:text-xl text-blue-100 font-semibold leading-tight whitespace-nowrap">Policies<br>Issued</div>
                    </div>
                </div>
                
                <!-- Claims Processed Card -->
                <div class="bg-white bg-opacity-10 backdrop-blur-sm rounded-2xl p-8 lg:p-10 xl:p-12 border border-white border-opacity-20 hover:bg-opacity-15 hover:scale-105 transition-all duration-300 min-h-[280px] flex items-center justify-center" 
                     x-data="{ count: 0, target: %d }" 
                     x-init="setTimeout(() => { let interval = setInterval(() => { if(count < target) { count += Math.ceil(target/50); if(count > target) count = target; } else { clearInterval(interval); } }, 20); }, 400)">
                    <div class="flex flex-col items-center text-center w-full">
                        <div class="w-20 h-20 lg:w-24 lg:h-24 bg-gradient-to-br from-purple-400 to-purple-500 rounded-2xl flex items-center justify-center mb-6 shadow-lg">
                            <i data-lucide="check-circle-2" class="w-10 h-10 lg:w-12 lg:h-12 text-white"></i>
                        </div>
                        <div class="text-6xl lg:text-7xl font-extrabold text-white mb-3 tracking-tight" x-text="count.toLocaleString() + '+'">%d+</div>
                        <div class="text-lg lg:text-xl text-blue-100 font-semibold leading-tight whitespace-nowrap">Claims<br>Processed</div>
                    </div>
                </div>
            </div>
            """,
            stats.get("totalUsers"), stats.get("totalUsers"),
            stats.get("activePolicyHolders"), stats.get("activePolicyHolders"),
            stats.get("totalPolicies"), stats.get("totalPolicies"),
            stats.get("approvedClaims"), stats.get("approvedClaims")
        );
    }
    
    /**
     * API endpoint for active policies (HTMX polling)
     */
    @GetMapping("/api/policies/active")
    @ResponseBody
    public String getActivePoliciesFragment() {
        List<Policy> policies = policyRepository.findByIsActive(true);
        
        StringBuilder html = new StringBuilder();
        
        for (Policy policy : policies) {
            boolean isPremium = "PREMIUM".equals(policy.getType().name());
            String icon = switch (policy.getType()) {
                case FAMILY -> "users";
                case SENIOR -> "heart-pulse";
                case PREMIUM -> "crown";
                default -> "shield";
            };
            
            html.append(String.format("""
                <div class="%s bg-white rounded-2xl shadow-xl overflow-hidden hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border border-gray-100 min-h-[500px] flex flex-col">
                    %s
                    <div class="p-8 lg:p-10 flex-1 flex flex-col">
                        <div class="flex items-center justify-between mb-6">
                            <h3 class="text-2xl lg:text-3xl font-bold text-slate-900">%s</h3>
                            <div class="w-14 h-14 lg:w-16 lg:h-16 bg-gradient-to-br from-blue-100 to-blue-50 rounded-2xl flex items-center justify-center shadow-md transform transition-transform hover:scale-110 duration-300">
                                <i data-lucide="%s" class="w-7 h-7 lg:w-8 lg:h-8 text-blue-600"></i>
                            </div>
                        </div>
                        
                        <div class="mb-8">
                            <div class="flex items-baseline">
                                <span class="text-xl font-semibold text-slate-700 mr-2">LKR</span>
                                <span class="text-5xl lg:text-6xl font-bold bg-gradient-to-r from-blue-600 to-blue-500 bg-clip-text text-transparent">%,.0f</span>
                                <span class="text-slate-600 ml-2 text-lg">/month</span>
                            </div>
                            <div class="text-base text-slate-500 mt-2 font-medium">
                                Coverage: LKR %,.0f
                            </div>
                        </div>
                        
                        <div class="space-y-4 mb-8 flex-1">
                            <div class="flex items-start space-x-3">
                                <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                    <i data-lucide="check" class="w-4 h-4 text-green-600"></i>
                                </div>
                                <span class="text-slate-700 text-lg">Hospitalization coverage</span>
                            </div>
                            <div class="flex items-start space-x-3">
                                <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                    <i data-lucide="check" class="w-4 h-4 text-green-600"></i>
                                </div>
                                <span class="text-slate-700 text-lg">Pre & post hospitalization</span>
                            </div>
                            <div class="flex items-start space-x-3">
                                <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                    <i data-lucide="check" class="w-4 h-4 text-green-600"></i>
                                </div>
                                <span class="text-slate-700 text-lg">Ambulance charges covered</span>
                            </div>
                            <div class="flex items-start space-x-3">
                                <div class="w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                    <i data-lucide="check" class="w-4 h-4 text-green-600"></i>
                                </div>
                                <span class="text-slate-700 text-lg">24/7 customer support</span>
                            </div>
                            %s
                        </div>
                        
                        <div class="space-y-3 mt-auto">
                            <a href="/auth/register" class="block w-full bg-gradient-to-r from-blue-600 to-blue-500 text-white text-center py-4 rounded-xl font-bold text-lg hover:shadow-2xl transition-all transform hover:scale-105 duration-300">
                                Apply Now
                            </a>
                            <button class="block w-full border-2 border-slate-300 text-slate-700 text-center py-4 rounded-xl font-semibold text-lg hover:bg-slate-50 hover:border-blue-400 transition-all duration-300">
                                Learn More
                            </button>
                        </div>
                    </div>
                </div>
                """,
                isPremium ? "ring-2 ring-orange-400" : "",
                isPremium ? "<div class=\"bg-gradient-to-r from-orange-500 to-orange-400 text-white text-center py-3 font-bold text-base shadow-md\">⭐ MOST POPULAR PLAN</div>" : "",
                policy.getName(),
                icon,
                policy.getPremiumAmount().doubleValue(),
                policy.getCoverageAmount().doubleValue(),
                (isPremium || "FAMILY".equals(policy.getType().name())) ? 
                    "<div class=\"flex items-start space-x-3\"><div class=\"w-6 h-6 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5\"><i data-lucide=\"check\" class=\"w-4 h-4 text-green-600\"></i></div><span class=\"text-slate-700 text-lg\">Annual health checkups included</span></div>" : ""
            ));
        }
        
        return html.toString();
    }
    
    /**
     * Helper method to get statistics from database
     * Returns REAL data only - no fallback placeholders
     */
    private Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total registered users - PURE DATABASE COUNT
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Active policy holders (users with role POLICY_HOLDER) - PURE DATABASE COUNT
        long activePolicyHolders = userRepository.countByRole(com.virul.medisure.model.User.UserRole.POLICY_HOLDER);
        stats.put("activePolicyHolders", activePolicyHolders);
        
        // Total policies issued - PURE DATABASE COUNT
        long totalPolicies = policyRepository.count();
        stats.put("totalPolicies", totalPolicies);
        
        // Approved claims (APPROVED_BY_FINANCE status) - PURE DATABASE COUNT
        long approvedClaims = claimRepository.countByStatus(com.virul.medisure.model.Claim.ClaimStatus.APPROVED_BY_FINANCE);
        stats.put("approvedClaims", approvedClaims);
        
        return stats;
    }
}

