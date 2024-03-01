package nextstep.subway.domain;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FareCalculatorImpl implements FareCalculator {

    private static final int DEFAULT_FARE = 1_250;

    private final List<FareCalculateOption> fareCalculateOptions;

    public FareCalculatorImpl() {
        this(List.of(
            new FareCalculateOption()
                .setDistanceOver(10)
                .setDistanceUnder(50)
                .setChargingUnitDistance(5)
                .setFare(100)
                .build(),
            new FareCalculateOption()
                .setDistanceOver(50)
                .setDistanceUnder(Integer.MAX_VALUE)
                .setChargingUnitDistance(8)
                .setFare(100)
                .build()
        ));
    }

    public FareCalculatorImpl(List<FareCalculateOption> fareCalculateOptions) {
        this.fareCalculateOptions = fareCalculateOptions;
    }

    public int calculateFare(int distance) {
        int fare = DEFAULT_FARE;

        for (FareCalculateOption calculateOption : fareCalculateOptions) {
            if (!calculateOption.isCalculateTarget(distance)) {
                continue;
            }

            fare += calculateOverFare(
                calculateOption.getOverDistance(distance),
                calculateOption.getChargingUnitDistance(),
                calculateOption.getFare()
            );
        }

        return fare;
    }
}
