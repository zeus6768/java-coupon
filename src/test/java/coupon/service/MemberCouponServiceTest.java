package coupon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import coupon.Fixtures;
import coupon.domain.MemberCoupon;
import coupon.repository.CouponRepository;
import coupon.repository.MemberCouponRepository;
import coupon.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberCouponServiceTest {

    @Autowired
    private MemberCouponService sut;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원에게 쿠폰 발급 성공")
    void issue_coupon_success() {
        // given
        var coupon = couponRepository.save(Fixtures.coupon_1);
        var member = memberRepository.save(Fixtures.member);

        // when
        var actual = sut.issueMemberCoupon(coupon, member);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName("한 회원에게 5장이 넘는 쿠폰을 발급하면 예외 발생")
    void issue_coupon_fail() {
        // given
        var member = memberRepository.save(Fixtures.member);
        var coupon = couponRepository.save(Fixtures.coupon_1);
        int maxCouponCount = 5;

        // when
        for (int i = 0; i < maxCouponCount; i++) {
            sut.issueMemberCoupon(coupon, member);
        }

        // then
        assertThatThrownBy(() -> sut.issueMemberCoupon(coupon, member))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("한 회원에게 최대 5장까지 발급할 수 있습니다.");
    }

    @Test
    @DisplayName("회원의 쿠폰 목록 조회: 쿠폰의 정보를 모두 볼 수 있음")
    void findAllCouponByMember() {
        // given
        var member = memberRepository.save(Fixtures.member);
        var coupon1 = couponRepository.save(Fixtures.coupon_1);
        var coupon2 = couponRepository.save(Fixtures.coupon_1);
        var coupon3 = couponRepository.save(Fixtures.coupon_2);
        var coupon4 = couponRepository.save(Fixtures.coupon_2);
        var memberCoupons = List.of(
                new MemberCoupon(member, coupon1),
                new MemberCoupon(member, coupon2),
                new MemberCoupon(member, coupon3),
                new MemberCoupon(member, coupon4));
        memberCouponRepository.saveAll(memberCoupons);

        // when
        var actual = sut.findAllCouponByMember(member);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(4),
                () -> assertThat(actual).containsExactlyInAnyOrder(coupon1, coupon2, coupon3, coupon4)
        );
    }
}
