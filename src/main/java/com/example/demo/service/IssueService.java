package com.example.demo.service;

import com.example.demo.model.Issue;
import com.example.demo.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);
    private final IssueRepository issueRepo;
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    
    @Value("${app.authority.email.pattern:authority@{city}.gov}")
    private String authorityEmailPattern;
    
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("^-?\\d+\\.\\d+,-?\\d+\\.\\d+$");

    public IssueService(IssueRepository issueRepo, EmailService emailService, RestTemplate restTemplate) {
        this.issueRepo = issueRepo;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    public Issue createIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }
        
        issue.setStatus(Issue.Status.PENDING);
        
        if (StringUtils.hasText(issue.getLocation()) && isValidCoordinate(issue.getLocation())) {
            issue.setCity(getCityFromLocation(issue.getLocation()));
        } else {
            issue.setCity("Unknown");
        }
        
        return issueRepo.save(issue);
    }

    public void escalateIssue(Long issueId) {
        if (issueId == null) {
            throw new IllegalArgumentException("Issue ID cannot be null");
        }
        
        Optional<Issue> issueOpt = issueRepo.findById(issueId);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setPriority(issue.getPriority() + 1);

            String subject = "Escalation Alert: " + issue.getCategory().name();
            String body = "Issue " + issue.getId() + " in city " + issue.getCity() + 
                          " has not been resolved. Priority Level: " + issue.getPriority();

            String authorityEmail = authorityEmailPattern.replace("{city}", 
                StringUtils.hasText(issue.getCity()) ? issue.getCity().toLowerCase(Locale.ROOT) : "unknown");
            
            try {
                emailService.sendEmail(authorityEmail, subject, body);
            } catch (Exception e) {
                logger.error("Failed to send escalation email for issue {}", issueId, e);
            }
            
            issueRepo.save(issue);
        } else {
            throw new IllegalArgumentException("Issue with ID " + issueId + " not found");
        }
    }
    
    private boolean isValidCoordinate(String location) {
        return COORDINATE_PATTERN.matcher(location).matches();
    }

    private String getCityFromLocation(String location) {
        try {
            if (!isValidCoordinate(location)) {
                return "Unknown";
            }
            
            String[] coords = location.split(",");
            double lat = Double.parseDouble(coords[0]);
            double lon = Double.parseDouble(coords[1]);
            
            // Validate coordinate ranges
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                return "Unknown";
            }
            
            String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("address")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> address = (Map<String, Object>) response.get("address");
                return address.getOrDefault("city", "Unknown").toString();
            }
            
            return "Unknown";
        } catch (RestClientException e) {
            logger.warn("Failed to fetch city from coordinates: {}", e.getMessage());
            return "Unknown";
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("Invalid coordinate format: {}", location);
            return "Unknown";
        }
    }
}
