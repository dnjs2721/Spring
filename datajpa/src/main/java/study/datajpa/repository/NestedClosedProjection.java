package study.datajpa.repository;

/**
 * Projection
 * 중첩 구조 처리
 */
public interface NestedClosedProjection {
    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo {
        String getTeamName();
    }
}
