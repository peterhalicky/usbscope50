# USBScope50

This repository contains software for the USBScope50 digital oscilloscope.

Description as well as some very useful hints can be found in the [old archived page and comments](https://peterhalicky.github.io/usbscope50). Future discussions should 
happen [here](https://github.com/phjr/usbscope50/discussions).

## Initial Setup for Windows

After extracting USBscope50-Java-Software-with-JRE.zip, place the folder in the following directory.

'''
C:\Program Files\Elan Digital Systems\USBscope50 Java Software
'''

After connecting USBscope50, start Java from 'start.bat'.

## VCP Driver Installation for Windows

The standard Virtual COM Port (VCP) driver does not support the VID/PID used by USBscope50. Therefore, a custom VCP driver must be created using Silicon Labs' custom driver creation software. If the custom VCP driver is self-signed, it will work properly on Windows with Secure Boot enabled.

> [!NOTE]
> Custom VCP drivers are based on signed drivers provided by Silicon Labs. Although the security issue is considered low, please select the target files carefully and proceed at your own risk when self-signing.

1. Required Software and Files

    - The test certificate registered in the 'Trusted Root Certification Authorities Certificate Store' on the local PC
      - Please refer to Microsoft's ["How to Test-Sign a Driver Package"](https://learn.microsoft.com/en-us/windows-hardware/drivers/install/how-to-test-sign-a-driver-package) to create a test certificate file (.cer).
        - Visual studio
        - Windows SDK
        - Windows Driver Kit
        - These installations are required.
    - CP210x Software package for Windows, includes VCP drivers
      - Please download and install from [Silicon Labs](https://www.silabs.com/interface/usb-bridges/classic/device.cp2102?tab=softwareandtools).

2. Creating a Driver with 'CP210x Driver Customization Utility (AN220)'

    1. Select the Driver to Create

        Check the driver type and the driver file output method. You can select "Executable Installer".

        - 'Virtual COM Port Driver Installation'
        - 'INF Files only'

        ![A](/READMEfigs/001.jpg)

    2. Editing the Device List

        Delete everything in the 'Device List' and add 'USBscope50'.

        - Device Type: 'CP2101/2/3/4'
        - VID = '10C4'
        - PID = 'F001'
        - Device Name: 'USBscope50'

        ![A](/READMEfigs/005b.jpg)

        ![A](/READMEfigs/005c.jpg)

    3. Check the Driver Configuration

        '''
        Install Type:           VCP Driver Set
        Company Name:           Silicon Labs
        Abbreviation:           10C4/F001
        VID/PID:                slabvcp.inf
        INF File Names:         CP210x USB to UART Bridge
        COM Device Name:        No
        Serial Enumeration?     No
        Selective Suspend?      No
        S. Suspend Timeout:     -
        Generate Installer?     No (or Yes)
        Product Name:           -
        Installer Name:         -
        Display Install?        -
        Copy Files?             -
        Relative Install Type:  -
        Target Directory:       -
        Display Uninstall?      -
        Remove Files?           -
        '''

        If 'Serial Enumeration' and/or 'S. Suspend Timeout' are enabled, go back a few pages and uncheck the 'Serial Enumeration Support' and 'Selective Suspend Support' checkboxes.

        If successful, an .inf file will be generated in the specified folder.

        ![A](/READMEfigs/008.jpg)

        ![A](/READMEfigs/009.jpg)

3. Generating Binary Drivers with 'inf2cat'
    
    Use 'inf2cat' to convert the .inf file to .cat. Note that you need to specify the folder where the .inf file is located, not the path to the .inf file.

    developer powershell for vs 2022 Code Sample
    '''
    inf2cat /drv:'your\driver\directory' /os:10_NI_X64
    '''

4. Signing Binary Drivers

    Embed your test certificate in the .cat file.([Reference](https://learn.microsoft.com/ja-jp/windows-hardware/drivers/install/test-signing-a-driver-package-s-catalog-file))

    developer powershell for vs 2022 Code Sample

    Embed
    '''
    Signtool sign /v /fd sha256 /s PrivateCertStore /n 'YourSignatureName' 'your\driver\binary.cat'
    '''

    Verification
    '''
    Signtool verify /pa /v 'your\driver\binary.cat'
    '''

5. Installing the Driver

    Right-click on the generated .inf file and install it.

6. VCP Driver Operation Check

    Connect USBscope50 to your PC and check that it is recognized in the Device Manager. If you are using a stack configuration, please also check that all USBscope50 devices are registered with different COM ports.

    'Device Manager' -> 'Ports (COM & LPT)' -> 'USBscope50(COM...)'