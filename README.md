# Qubership Testing Platform Export-Import Library

## Purpose
Export-Import Library is a library of utility classes staying behind export/import processes performed by
Qubership Testing Platform Export-Import Service (as a process orchestrator) and other QSTP Services (data owners).
So, 
1. A user initiates Export of some project data or Import of some archive containing exported data files,
2. Export-Import Service:
   3. receives user request, 
   4. prepares internal process(es) and data, according to data objects selected for export
   5. and sends requests to perform export (or import) of their data to all services-participants
6. Then Services-participants:
   7. receive requests from Export-Import Service,
   8. perform export (or import) of their data,
   9. report progress and status Export-Import Service,
   10. finally, send resulting file(s) to Export-Import Service
11. During this process, Export-Import Service:
    12. checks overall status, 
    13. and produces file with exported data archived (in case export),
    14. and makes it available for the user to download.

Export-Import Library is used by Export-Import Service and by other services-participants during above process,
to organize the process, to perform data exchange and to interact with other sides of the process.

## Local build

In IntelliJ IDEA, one can select 'github' Profile in Maven Settings menu on the right,
then expand Lifecycle dropdown of qubership-atp-export-import-lib module, then select 'clean' and 'install' 
options and click 'Run Maven Build' green arrow button on the top.

Or, one can execute the command:
```bash
mvn -P github clean install
```

## How to add dependency into a service
```xml
    <!-- Change version number if necessary -->
    <dependency>
        <groupId>org.qubership.atp.ei</groupId>
        <artifactId>qubership-atp-export-import-lib-node</artifactId>
        <version>0.2.40</version>
    </dependency>
```
