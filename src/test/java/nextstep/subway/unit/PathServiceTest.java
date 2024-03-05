package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.auth.domain.LoginMember;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.FareCalculator;
import nextstep.subway.domain.FareCalculatorImpl;
import nextstep.subway.domain.JGraphTPathFinderImpl;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    @Mock
    private LineRepository lineRepository;


    private PathService pathService;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10, 10, 0);

        이호선.addSection(역삼역, 선릉역, 10, 10);

        when(lineRepository.findAll()).thenReturn(List.of(이호선));

        FareCalculator fareCalculator = new FareCalculatorImpl();
        JGraphTPathFinderImpl jGraphTPathFinder = new JGraphTPathFinderImpl(fareCalculator);
        pathService = new PathService(lineRepository, jGraphTPathFinder, null);
    }

    @Test
    void 경로_조회_기능() {
        final PathResponse response = pathService.getPath(
            new PathRequest(강남역.getId(), 선릉역.getId()), null);

        assertThat(response.getDistance()).isEqualTo(20);
        assertThat(response.getStations().stream().map(StationResponse::getId).collect(Collectors.toList()))
            .contains(강남역.getId(), 역삼역.getId());
    }

    @Test
    void 출발지와_도착지가_같으면_익셉션을_던진다() {
        assertThatThrownBy(() -> pathService.getPath(new PathRequest(강남역.getId(), 강남역.getId()),
            null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 출발지_역이_없으면_익셉션을_던진다() {
        assertThatThrownBy(() -> pathService.getPath(new PathRequest(4L, 선릉역.getId()), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 도착지_역이_없으면_익셉션을_던진다() {
        assertThatThrownBy(() -> pathService.getPath(new PathRequest(강남역.getId(), 4L), null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
