package nextstep.subway.member.application;

import nextstep.subway.auth.domain.Authentication;
import nextstep.subway.auth.domain.User;
import nextstep.subway.auth.exception.AuthorizationException;
import nextstep.subway.auth.infrastructure.SecurityContext;
import nextstep.subway.auth.infrastructure.SecurityContextHolder;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(MemberRequest request) {
        Member member = memberRepository.save(request.toMember());
        return MemberResponse.of(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(RuntimeException::new);
        return MemberResponse.of(member);
    }

    public void updateMember(Long id, MemberRequest param) {
        checkPermission(id);
        Member member = memberRepository.findById(id).orElseThrow(RuntimeException::new);
        member.update(param.toMember());
    }

    public void deleteMember(Long id) {
        checkPermission(id);
        memberRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        return MemberResponse.of(member);
    }

    private void checkPermission(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AuthorizationException();
        }

        User user = (User) authentication.getPrincipal();

        if (!Objects.equals(id, user.getId())) {
            throw new AuthorizationException();
        }
    }
}