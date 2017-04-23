package hu.bme.mit.codemodel.rifle.tasks;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.utils.ResourceReader;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

public class QualifierSystem {
    public static final Logger logger = Logger.getLogger("codemodel");

    public void qualify(String branchId) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchId);
        final String initQualifierSystemQuery = ResourceReader.query("qualifier" + File.separator + "QualifierSystem");
        final Collection<String> qualifierQueries = ResourceReader.getQualifierQueries();
        final Collection<String> qualifierProviderQueries = ResourceReader.getQualifierProviderQueries();

        Driver driver = dbServices.getDriver();
        Session session = driver.session();

        // QualifierSystem initialization happens once
        try (Transaction tx = session.beginTransaction()) {
            tx.run(initQualifierSystemQuery);

            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // QualifierSystem's Qualifiers' initialization happens once
        for (String query : qualifierQueries) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run(query);

                tx.success();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // QualifierProviders propagate until there is no modification in the graph
        // Will be updated if ingraph arrives and we can detect changes
        try (Transaction tx = session.beginTransaction()) {
            for (int i = 0; i < 1000; i++) {
                for (String query : qualifierProviderQueries) {
                    tx.run(query);
                }
            }

            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
