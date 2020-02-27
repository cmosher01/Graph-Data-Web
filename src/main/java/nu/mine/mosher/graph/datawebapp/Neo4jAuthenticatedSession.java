package nu.mine.mosher.graph.datawebapp;

import nu.mine.mosher.graph.datawebapp.util.Utils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.Strings;

public class Neo4jAuthenticatedSession extends AuthenticatedWebSession {
    public Neo4jAuthenticatedSession(final Request request) {
        super(request);
    }

    @Override
    protected boolean authenticate(final String username, final String password) {
        System.out.println("authenticating user: "+username);
        if (Utils.store().haveSession()) {
            return true;
        }

        System.out.println("connecting to "+boltUrl());
        Utils.store().createSession(boltUrl(), username, password, Utils.app().packages());

        return Utils.store().haveSession();
    }

    private static String boltUrl() {
        String url = System.getenv("neo4j_url");
        if (Strings.isEmpty(url)) {
            url = "bolt://neo4j";
        }
        return url;
    }

    @Override
    public Roles getRoles() {
        return new Roles(Roles.ADMIN+","+Roles.USER);
    }
}
