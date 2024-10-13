package com.swpproject.koi_care_system.service.issue;

import com.swpproject.koi_care_system.dto.IssueDto;
import com.swpproject.koi_care_system.enums.RangeParameter;
import com.swpproject.koi_care_system.mapper.IssueMapper;
import com.swpproject.koi_care_system.models.Issue;
import com.swpproject.koi_care_system.models.IssueType;
import com.swpproject.koi_care_system.models.WaterParameters;
import com.swpproject.koi_care_system.repository.IssueRepository;
import com.swpproject.koi_care_system.repository.IssueTypeRepository;
import com.swpproject.koi_care_system.service.waterparameter.IWaterParameters;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IssueService implements IIssueService {
    IssueRepository issueRepository;
    IssueTypeRepository issueTypeRepository;
    IssueMapper issueMapper;

    public void detectIssues(WaterParameters waterParameters) {
        List<Issue> existIssue = issueRepository.findAll();
        if (!existIssue.isEmpty()) {
            issueRepository.deleteAll(existIssue);
        }
        for (RangeParameter parameter : RangeParameter.values()) {
            double value = getParameterValue(waterParameters, parameter);

            if (parameter.isLow(value) || parameter.isHigh(value)) {
                String conditionType = parameter.isLow(value) ? "LOW" : "HIGH";
                createIssue(conditionType, parameter, waterParameters);
            }
        }
    }

    private void createIssue(String conditionType, RangeParameter parameter, WaterParameters waterParameters) {
        IssueType issueType = issueTypeRepository.findByParameterTypeAndConditionType(parameter, conditionType);
        boolean issueExist = issueRepository.existsByWaterParametersAndDescription(waterParameters, conditionType + " " + parameter.name());

        if (!issueExist) {
            Issue issue = new Issue();
            issue.setName(parameter.getName());
            issue.setWaterParameters(waterParameters);
            issue.setIssueType(issueType);
            issue.setDescription(conditionType + " " + parameter.name());
            issueRepository.save(issue);
        }
    }

    public List<IssueDto> getIssue(Long waterParametersId) {
        return issueRepository.findByWaterParametersId(waterParametersId).stream().map(issueMapper::mapToIssueDto).toList();
    }

//    @Override
//    public List<IssueDto> getCurrentIssueByKoiPondID(Long koiPondId) {
//        return issueRepository.findByWaterParametersId(waterParameters.getLatestWaterParametersByKoiPondId(koiPondId).getId()).stream().map(issueMapper::mapToIssueDto).toList();
//    }


    private double getParameterValue(WaterParameters waterParameters, RangeParameter parameter) {
        return switch (parameter) {
            case NO2 -> waterParameters.getNitrite();
            case NO3 -> waterParameters.getNitrate();
            case PO4 -> waterParameters.getPhosphate();
            case NH4 -> waterParameters.getAmmonium();
            case GH -> waterParameters.getHardness();
            case O2 -> waterParameters.getOxygen();
            case TEMPERATURE -> waterParameters.getTemperature();
            case PH -> waterParameters.getPhValue();
            case KH -> waterParameters.getCarbonHardness();
            case CO2 -> waterParameters.getCarbonDioxide();
            case SALT -> waterParameters.getSalt();
            case CHLORINE -> waterParameters.getTotalChlorine();
            case OUTDOORTEMP -> waterParameters.getTemp();
            default -> throw new IllegalArgumentException("Unknown parameter: " + parameter);
        };
    }
}