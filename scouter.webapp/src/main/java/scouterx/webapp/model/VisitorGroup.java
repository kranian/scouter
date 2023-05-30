package scouterx.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import scouter.lang.pack.MapPack;
import scouter.lang.value.ListValue;
import scouter.util.CastUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by csk746(csk746@naver.com) on 2017. 10. 15..
 */
@Getter
@ToString
@AllArgsConstructor
@Builder
public class VisitorGroup {

    private String time;

    private long value;

    public static VisitorGroup of(MapPack mapPack){
        return VisitorGroup.builder().time(mapPack.getText("time")).value(mapPack.getLong("value")).build();
    }
    public static Stream<VisitorGroup> collect(MapPack mapPack){
//        System.out.println(mapPack.getList("time"));
        ListValue time  = mapPack.getList("time");
        ListValue value  = mapPack.getList("value");
        return IntStream.iterate(0,(iter)->iter+1)
                .limit(time.size())
                .mapToObj(iter ->VisitorGroup.builder().time(CastUtil.cString(time.getLong(iter))).value(value.getLong(iter)).build())
                .collect(Collectors.toList())
                .stream();


    }
}
