import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class RandomTeamGenerator {

  public static class Member {

    private String name;
    private Integer rank;
    private Role role;
    private String email;

    public Member(String name, Integer rank, Role role, String email) {
      this.name = name;
      this.rank = rank;
      this.role = role;
      this.email = email;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Integer getRank() {
      return role.getId() * rank;
    }

    public void setRank(Integer rank) {
      this.rank = rank;
    }

    public Role getRole() {
      return role;
    }

    public void setRole(Role role) {
      this.role = role;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  public enum Role {
    ATT(5, "Forward"),
    DIF(1, "Defender"),
    PORT(10, "Goalkeeper");
    private Integer id;
    private String desc;

    Role(int id, String desc) {
      this.id = id;
      this.desc = desc;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public static Role getById(int id) {
      return Arrays.stream(values()).filter(t -> t.getId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }
  }

  public static void main(String[] args) throws IOException {

    Map<String, List<Member>> teamMap = teamInitializer();
    ArrayList<Member> names = getMembersFromFile();
    final Map<Integer, List<Member>> orderedTeamPlayerMapByRank =
      new TreeMap<>(names.stream().collect(Collectors.groupingBy(Member::getRank)));
    orderedTeamPlayerMapByRank.forEach((k, v) -> {
      fillRankedRandomTeams(teamMap, v);
    });
    teamMap.forEach((k, v) -> printTeams(k, v));
  }

  private static Map<String, List<Member>> teamInitializer() {
    System.out.println("Enter the number of teams you want to form: ");
    Scanner input = new Scanner(System.in);
    int n = input.nextInt();
    System.out.println("Set up " + n + " Teams");
    Map<String, List<Member>> teamMap = new HashMap<>();
    for (int i = 0; i < n; i++) {
      teamMap.put("Team" + i, new ArrayList<>());
    }
    return teamMap;
  }

  private static void printTeams(String k, List<Member> v) {
    System.out.println(k + "#" + v.size() + ": " + "\n\r" + v.stream()
                                                              .map(e -> e.getName() + "; " + e.getRole().desc + "; " + e.getEmail())
                                                              .collect(Collectors.joining("\n\r")));
  }

  private static void fillRankedRandomTeams(Map<String, List<Member>> teamMap, List<Member> v) {
    while (!v.isEmpty()) {
      Collections.shuffle(v);
      final String key = getKey(teamMap);
      final List<Member> members = teamMap.get(key);
      members.add(v.remove(0));
    }
  }

  private static ArrayList<Member> getMembersFromFile() throws IOException {
    ArrayList<Member> names = new ArrayList<>();
    FileReader reader = new FileReader("Players" + ".csv");
    BufferedReader bufferedReader = new BufferedReader(reader);
    String line = "";
    while ((line = bufferedReader.readLine()) != null) {
      if (!line.isEmpty()) {
        final String[] split = line.split(",");
        final String name = split[0];
        final int rank = Integer.parseInt(split[1]);
        final Role role = Role.getById(Integer.parseInt(split[2]));
        final String email = split[3];
        Member member = new Member(name, rank, role, email);
        names.add(member);
      }
    }
    reader.close();
    return names;
  }

  private static String getKey(Map<String, List<Member>> teamMap) {
    return teamMap.entrySet().stream().min(Comparator.comparing(e -> e.getValue().size())).get().getKey();
  }
}
